package sh.abijith.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sh.abijith.authservice.dto.AuthResponse;
import sh.abijith.authservice.dto.LoginRequest;
import sh.abijith.authservice.dto.RefreshTokenRequest;
import sh.abijith.authservice.dto.RegisterRequest;
import sh.abijith.authservice.exception.InvalidCredentialsException;
import sh.abijith.authservice.exception.UserAlreadyExistsException;
import sh.abijith.authservice.exception.UserNotFoundException;
import sh.abijith.authservice.model.User;
import sh.abijith.authservice.repository.UserRepository;
import sh.abijith.authservice.util.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
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
}
