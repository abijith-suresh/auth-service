package sh.abijith.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sh.abijith.authservice.dto.AuthResponse;
import sh.abijith.authservice.dto.LoginRequest;
import sh.abijith.authservice.dto.RefreshTokenRequest;
import sh.abijith.authservice.dto.RegisterRequest;
import sh.abijith.authservice.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Registers a new user with the provided registration details.
     *
     * @param request the registration request containing email, password, and user profile info
     * @return the response containing a message confirming successful registration
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Logs in the user by validating their credentials and returns access and refresh tokens.
     *
     * @param request the login request containing email and password
     * @return the response containing access token, refresh token, and success message
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Refreshes the access token using the provided refresh token.
     *
     * @param refreshTokenRequest the request containing a valid refresh token
     * @return the response containing a new access token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authService.refresh(refreshTokenRequest));
    }

    /**
     * Validates a given JWT token to ensure it's not expired or malformed.
     *
     * @param token the JWT token to validate
     * @return 200 OK if token is valid
     */
    @GetMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        authService.validateToken(token);
        return ResponseEntity.ok("Token is valid");
    }
}
