package sh.abijith.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sh.abijith.authservice.dto.AuthResponse;
import sh.abijith.authservice.dto.LoginRequest;
import sh.abijith.authservice.dto.RegisterRequest;
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
        var user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, "");
    }

    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, "");
    }
}
