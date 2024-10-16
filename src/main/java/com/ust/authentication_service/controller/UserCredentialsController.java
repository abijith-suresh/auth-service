package com.ust.authentication_service.controller;

import com.ust.authentication_service.entity.UserCredentialEntity;
import com.ust.authentication_service.service.JwtService;
import com.ust.authentication_service.service.UserCredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class UserCredentialsController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public UserCredentialEntity register(@RequestBody UserCredentialEntity user){
        return userCredentialService.register(user);
    }

    @GetMapping("/validate/token")
    public boolean validateToken(@RequestParam String token){
        return userCredentialService.verify(token);
    }

    @PostMapping("/validate/user")
    public String getToken(@RequestBody UserCredentialEntity user){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword())
        );
        if (authentication.isAuthenticated())
            return userCredentialService.generateToken(user.getName());
        return "User not authenticated";
    }
}

