package com.example.bookmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.bookmanagement.entity.User;
import com.example.bookmanagement.repository.UserRepository;

@Configuration
public class InitUserConfig {

    @Bean
    public org.springframework.boot.CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("user1") == null) {
                User u = new User();
                u.setUsername("user1");
                u.setPassword(encoder.encode("password")); // ログイン: user1 / password
                u.setRole("USER");
                userRepository.save(u);
            }
        };
    }
}
