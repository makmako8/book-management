package com.example.bookmanagement.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bookmanagement.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
