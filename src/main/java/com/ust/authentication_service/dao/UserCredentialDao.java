package com.ust.authentication_service.dao;

import com.ust.authentication_service.entity.UserCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialDao extends JpaRepository<UserCredentialEntity, Integer> {
    public Optional<UserCredentialEntity> findByName(String name);
}
