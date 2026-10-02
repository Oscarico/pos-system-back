package com.osulaa.possystem.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.osulaa.possystem.entity.Role;
import com.osulaa.possystem.entity.User;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role role;

    private User activeUser;
    private User inactiveUser;
    private User anotherActiveUser;

    @BeforeEach
    void setUp() {

        String timestamp = String.valueOf(System.currentTimeMillis());

        role = Role.builder()
                .name("ADMIN TEST " + timestamp)
                .build();

        role = roleRepository.save(role);

        activeUser = User.builder()
                .username("usuario.test." + timestamp)
                .password("password123")
                .name("Juan")
                .lastName("Perez")
                .active(true)
                .role(role)
                .build();

        inactiveUser = User.builder()
                .username("inactivo.test." + timestamp)
                .password("password123")
                .name("Pedro")
                .lastName("Gomez")
                .active(false)
                .role(role)
                .build();

        anotherActiveUser = User.builder()
                .username("maria.test." + timestamp)
                .password("password123")
                .name("Maria")
                .lastName("Lopez")
                .active(true)
                .role(role)
                .build();

        List<User> savedUsers = userRepository.saveAll(List.of(
                activeUser,
                inactiveUser,
                anotherActiveUser));

        activeUser = savedUsers.get(0);
        inactiveUser = savedUsers.get(1);
        anotherActiveUser = savedUsers.get(2);
    }

    @Test
    void findByUsername_deberiaEncontrarUsuario() {

        Optional<User> result = userRepository
                .findByUsername(activeUser.getUsername());

        assertTrue(result.isPresent());
        assertEquals(
                activeUser.getUsername(),
                result.get().getUsername());
    }

    @Test
    void existsByUsername_deberiaRegresarTrueSiExiste() {

        boolean result = userRepository
                .existsByUsername(activeUser.getUsername());

        assertTrue(result);
    }

    @Test
    void existsByUsername_deberiaRegresarFalseSiNoExiste() {

        boolean result = userRepository
                .existsByUsername("usuario.inexistente." + System.currentTimeMillis());

        assertFalse(result);
    }

    @Test
    void findByUsernameAndActiveTrue_deberiaEncontrarUsuarioActivo() {

        Optional<User> result = userRepository
                .findByUsernameAndActiveTrue(activeUser.getUsername());

        assertTrue(result.isPresent());
        assertEquals(
                activeUser.getUsername(),
                result.get().getUsername());
    }

    @Test
    void findByUsernameAndActiveTrue_noDeberiaEncontrarUsuarioInactivo() {

        Optional<User> result = userRepository
                .findByUsernameAndActiveTrue(inactiveUser.getUsername());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdAndActiveTrue_deberiaEncontrarUsuarioActivo() {

        Optional<User> result = userRepository
                .findByIdAndActiveTrue(activeUser.getId());

        assertTrue(result.isPresent());
        assertEquals(
                activeUser.getName(),
                result.get().getName());
    }

    @Test
    void findByIdAndActiveTrue_noDeberiaEncontrarUsuarioInactivo() {

        Optional<User> result = userRepository
                .findByIdAndActiveTrue(inactiveUser.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByNameAndActiveTrue_deberiaEncontrarUsuarioActivo() {

        Optional<User> result = userRepository
                .findByNameAndActiveTrue(activeUser.getName());

        assertTrue(result.isPresent());
        assertEquals(
                activeUser.getName(),
                result.get().getName());
    }

    @Test
    void findByNameAndActiveTrue_noDeberiaEncontrarUsuarioInactivo() {

        Optional<User> result = userRepository
                .findByNameAndActiveTrue(inactiveUser.getName());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByActiveTrue_deberiaRegresarSoloUsuariosActivos() {

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("name").ascending());

        Page<User> result = userRepository.findByActiveTrue(pageable);

        assertTrue(result.getTotalElements() >= 2);
        assertTrue(result.getContent().stream().allMatch(User::getActive));
    }

    @Test
    void findByNameContainingIgnoreCaseAndActiveTrue_deberiaBuscarPorNombre() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> result = userRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(
                        "jUaN",
                        pageable);

        assertTrue(result.getTotalElements() >= 1);

        assertTrue(result.getContent().stream()
                .anyMatch(user -> user.getName().equals(activeUser.getName())));
    }
}