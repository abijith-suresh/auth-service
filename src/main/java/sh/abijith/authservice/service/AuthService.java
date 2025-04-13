package sh.abijith.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sh.abijith.authservice.client.UserClient;
import sh.abijith.authservice.config.LoginSecurityProperties;
import sh.abijith.authservice.dto.*;
import sh.abijith.authservice.exception.*;
import sh.abijith.authservice.mapper.UserMapper;
import sh.abijith.authservice.model.User;
import sh.abijith.authservice.repository.UserRepository;
import sh.abijith.authservice.util.JwtService;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginSecurityProperties loginSecurityProps;
    private final UserClient userClient;
    private final UserMapper userMapper;

    /**
     * Registers a new user and creates a corresponding user profile in the user service.
     *
     * @param request the registration request containing email, password, and user profile info
     * @return response message confirming registration
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        var user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(request.getRoles());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        var profile = userMapper.toUserProfileRequest(user, request);
        userClient.createUserProfile(profile);

        return new AuthResponse(null, null, "Registration is Successful. Please Login Again");
    }

    /**
     * Authenticates the user and generates JWT tokens upon successful login.
     *
     * @param request the login request containing email and password
     * @return the response containing access token, refresh token, and success message
     * @throws InvalidCredentialsException if the credentials are incorrect
     * @throws AccountLockedException if the account is locked due to too many failed attempts
     */
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isLocked()) {
            if (isLockExpired(user)) {
                unlock(user);
            } else {
                throw new AccountLockedException("Account is temporarily locked. Try again later.");
            }
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            incrementFailedAttempts(user);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        resetFailedAttempts(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, "Login Successful");
    }

    /**
     * Generates a new access token from the provided refresh token.
     *
     * @param refreshTokenRequest the request containing a valid refresh token
     * @return response containing new access token and original refresh token
     * @throws InvalidRefreshTokenException if the token is invalid
     */
    public AuthResponse refresh(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        String newAccessToken = jwtService.generateTokenFromRefreshToken(refreshToken);

        return new AuthResponse(newAccessToken, refreshToken, "Token refreshed successfully");
    }

    /**
     * Validates the structure and expiration of a given JWT token.
     *
     * @param token the JWT token to validate
     * @throws InvalidTokenException if the token is malformed, expired, or invalid
     */
    public void validateToken(String token){
        jwtService.validateToken(token);
    }


    /**
     * Increments failed login attempts and locks account if threshold is exceeded.
     *
     * @param user the user whose login attempt failed
     */
    private void incrementFailedAttempts(User user) {
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        if (user.getFailedAttempts() >= loginSecurityProps.getMaxFailedAttempts()) {
            user.setLocked(true);
            user.setLockTime(new Date());
        }
        userRepository.save(user);
    }

    /**
     * Resets the failed login attempt count and unlocks the user account.
     *
     * @param user the user to reset
     */
    private void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setLocked(false);
        user.setLockTime(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Checks whether the user's account lock duration has expired.
     *
     * @param user the user to check
     * @return true if lock has expired, false otherwise
     */
    private boolean isLockExpired(User user) {
        long lockDurationMillis = (long) loginSecurityProps.getLockDurationMinutes() * 60 * 1000;
        return new Date().getTime() - user.getLockTime().getTime() >= lockDurationMillis;
    }

    /**
     * Unlocks a user account and resets failure counters.
     *
     * @param user the user to unlock
     */
    private void unlock(User user) {
        user.setLocked(false);
        user.setLockTime(null);
        user.setFailedAttempts(0);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

}
