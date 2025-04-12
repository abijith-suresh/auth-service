package sh.abijith.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sh.abijith.authservice.config.LoginSecurityProperties;
import sh.abijith.authservice.dto.AuthResponse;
import sh.abijith.authservice.dto.LoginRequest;
import sh.abijith.authservice.dto.RefreshTokenRequest;
import sh.abijith.authservice.dto.RegisterRequest;
import sh.abijith.authservice.exception.AccountLockedException;
import sh.abijith.authservice.exception.InvalidCredentialsException;
import sh.abijith.authservice.exception.UserAlreadyExistsException;
import sh.abijith.authservice.exception.UserNotFoundException;
import sh.abijith.authservice.model.User;
import sh.abijith.authservice.repository.UserRepository;
import sh.abijith.authservice.util.JwtService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginSecurityProperties loginSecurityProps;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        var user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return new AuthResponse(null, null, "Registration is Successful. Please Login Again");
    }

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

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken, "Login Successful");
    }

    public AuthResponse refresh(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        String newAccessToken = jwtService.generateTokenFromRefreshToken(refreshToken);

        return new AuthResponse(newAccessToken, refreshToken, "Token refreshed successfully");
    }

    private void incrementFailedAttempts(User user) {
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        if (user.getFailedAttempts() >= loginSecurityProps.getMaxFailedAttempts()) {
            user.setLocked(true);
            user.setLockTime(new Date());
        }
        userRepository.save(user);
    }

    private void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setLocked(false);
        user.setLockTime(null);
        userRepository.save(user);
    }

    private boolean isLockExpired(User user) {
        long lockDurationMillis = loginSecurityProps.getLockDurationMinutes() * 60 * 1000;
        return new Date().getTime() - user.getLockTime().getTime() >= lockDurationMillis;
    }

    private void unlock(User user) {
        user.setLocked(false);
        user.setLockTime(null);
        user.setFailedAttempts(0);
        userRepository.save(user);
    }

}
