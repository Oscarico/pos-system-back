package com.osulaa.possystem.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.osulaa.possystem.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByNameAndActiveTrue(String name);

    Optional<Client> findByEmailAndActiveTrue(String email);

    Optional<Client> findByIdAndActiveTrue(Long id);

    Page<Client> findByActiveTrue(Pageable pageable);

    Page<Client> findByNameContainingIgnoreCaseAndActiveTrue(
            String name,
            Pageable pageable);
}
