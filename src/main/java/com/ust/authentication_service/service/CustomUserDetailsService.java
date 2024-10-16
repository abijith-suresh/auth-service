package com.ust.authentication_service.service;

import com.ust.authentication_service.dao.UserCredentialDao;
import com.ust.authentication_service.entity.UserCredentialEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserCredentialDao userCredentialDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserCredentialEntity> user = userCredentialDao.findByName(username);
        return user.map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Username/password not valid"));
    }
}
