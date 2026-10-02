package com.osulaa.possystem.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.osulaa.possystem.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByUsernameAndActiveTrue(String username);

    Optional<User> findByIdAndActiveTrue(Long id);

    Optional<User> findByNameAndActiveTrue(String name);

    Page<User> findByActiveTrue(Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseAndActiveTrue(
            String name,
            Pageable pageable);

}
