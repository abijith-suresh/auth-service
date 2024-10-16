package com.ust.authentication_service.service;

import com.ust.authentication_service.dao.UserCredentialDao;
import com.ust.authentication_service.entity.UserCredentialEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCredentialService {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserCredentialDao userCredentialDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserCredentialEntity register(UserCredentialEntity user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userCredentialDao.saveAndFlush(user);
    }

    public boolean verify(String token){
        jwtService.validateToken(token);
        return true;
    }

    public String generateToken(String name){
        return jwtService.generateToken(name);
    }
}
