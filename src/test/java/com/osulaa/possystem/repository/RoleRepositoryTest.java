package com.osulaa.possystem.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.osulaa.possystem.entity.Role;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    private Role adminRole;
    private Role cashierRole;

    @BeforeEach
    void setUp() {

        adminRole = Role.builder()
                .name("ADMIN_TEST_" + System.currentTimeMillis())
                .build();

        cashierRole = Role.builder()
                .name("CASHIER_TEST_" + System.currentTimeMillis())
                .build();

        adminRole = roleRepository.save(adminRole);
        cashierRole = roleRepository.save(cashierRole);
    }

    @Test
    void findByName_deberiaEncontrarRol() {

        Optional<Role> result = roleRepository.findByName(adminRole.getName());

        assertTrue(result.isPresent());
        assertEquals(adminRole.getName(), result.get().getName());
    }

    @Test
    void findByName_noDeberiaEncontrarRolInexistente() {

        Optional<Role> result = roleRepository.findByName("ROL_INEXISTENTE");

        assertTrue(result.isEmpty());
    }
}