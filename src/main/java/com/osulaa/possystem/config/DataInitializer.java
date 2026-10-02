package com.osulaa.possystem.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.osulaa.possystem.entity.Role;
import com.osulaa.possystem.entity.User;
import com.osulaa.possystem.repository.RoleRepository;
import com.osulaa.possystem.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository, UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {
        return args -> {

            if (roleRepository.count() == 0) {

                roleRepository.save(
                        Role.builder()
                                .name("ADMIN")
                                .build());
                roleRepository.save(
                        Role.builder()
                                .name("CASHIER")
                                .build());
            }

            if (!userRepository.existsByUsername("admin")) {
                Role adminRole = roleRepository
                        .findByName("ADMIN")
                        .orElseThrow();

                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("Admin123"))
                        .name("Administrador")
                        .lastName("Sistema")
                        .active(true)
                        .role(adminRole)
                        .build();

                userRepository.save(admin);
            }
        };
    }
}
