package com.example.dockersampleapp.repository;

import com.example.dockersampleapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}