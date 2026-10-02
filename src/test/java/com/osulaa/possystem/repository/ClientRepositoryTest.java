package com.osulaa.possystem.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.osulaa.possystem.entity.Client;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    private Client activeClient;
    private Client inactiveClient;
    private Client anotherActiveClient;

    @BeforeEach
    void setUp() {

        String timestamp = String.valueOf(System.currentTimeMillis());

        activeClient = Client.builder()
                .name("Juan Perez Test " + timestamp)
                .email("juan.test." + timestamp + "@example.com")
                .active(true)
                .build();

        inactiveClient = Client.builder()
                .name("Pedro Gomez Test " + timestamp)
                .email("pedro.test." + timestamp + "@example.com")
                .active(false)
                .build();

        anotherActiveClient = Client.builder()
                .name("Maria Lopez Test " + timestamp)
                .email("maria.test." + timestamp + "@example.com")
                .active(true)
                .build();

        List<Client> savedClients = clientRepository.saveAll(List.of(
                activeClient,
                inactiveClient,
                anotherActiveClient));

        activeClient = savedClients.get(0);
        inactiveClient = savedClients.get(1);
        anotherActiveClient = savedClients.get(2);
    }

    @Test
    void findByNameAndActiveTrue_deberiaEncontrarClienteActivo() {

        Optional<Client> result = clientRepository
                .findByNameAndActiveTrue(activeClient.getName());

        assertTrue(result.isPresent());
        assertEquals(activeClient.getName(), result.get().getName());
    }

    @Test
    void findByNameAndActiveTrue_noDeberiaEncontrarClienteInactivo() {

        Optional<Client> result = clientRepository
                .findByNameAndActiveTrue(inactiveClient.getName());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByEmailAndActiveTrue_deberiaEncontrarClienteActivo() {

        Optional<Client> result = clientRepository
                .findByEmailAndActiveTrue(activeClient.getEmail());

        assertTrue(result.isPresent());
        assertEquals(activeClient.getEmail(), result.get().getEmail());
    }

    @Test
    void findByEmailAndActiveTrue_noDeberiaEncontrarClienteInactivo() {

        Optional<Client> result = clientRepository
                .findByEmailAndActiveTrue(inactiveClient.getEmail());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdAndActiveTrue_deberiaEncontrarClienteActivo() {

        Optional<Client> result = clientRepository
                .findByIdAndActiveTrue(activeClient.getId());

        assertTrue(result.isPresent());
        assertEquals(activeClient.getName(), result.get().getName());
    }

    @Test
    void findByIdAndActiveTrue_noDeberiaEncontrarClienteInactivo() {

        Optional<Client> result = clientRepository
                .findByIdAndActiveTrue(inactiveClient.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByActiveTrue_deberiaRegresarSoloClientesActivos() {

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("name").ascending());

        Page<Client> result = clientRepository.findByActiveTrue(pageable);

        assertTrue(result.getTotalElements() >= 2);
        assertTrue(result.getContent().stream().allMatch(Client::getActive));
    }

    @Test
    void findByNameContainingIgnoreCaseAndActiveTrue_deberiaBuscarPorNombre() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Client> result = clientRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(
                        "jUaN",
                        pageable);

        assertTrue(result.getTotalElements() >= 1);
        assertTrue(result.getContent().stream()
                .anyMatch(client -> client.getName().equals(activeClient.getName())));
    }
}