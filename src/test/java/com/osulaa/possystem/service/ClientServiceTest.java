package com.osulaa.possystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.osulaa.possystem.dto.client.ClientRequest;
import com.osulaa.possystem.dto.client.ClientResponse;
import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.entity.Client;
import com.osulaa.possystem.exception.DuplicateResourceException;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.mapper.ClientMapper;
import com.osulaa.possystem.mapper.PageMapper;
import com.osulaa.possystem.repository.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private Page<Client> clientPage;

    @Mock
    private PageResponse<ClientResponse> pageResponse;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private ClientResponse clientResponse;
    private ClientRequest clientRequest;

    @BeforeEach
    void setUp() {

        client = Client.builder()
                .id(1L)
                .name("Juan Pérez")
                .phone("5512345678")
                .email("juan@email.com")
                .active(true)
                .build();

        clientResponse = ClientResponse.builder()
                .id(1L)
                .name("Juan Pérez")
                .phone("5512345678")
                .email("juan@email.com")
                .active(true)
                .build();

        clientRequest = new ClientRequest();
        clientRequest.setName("Juan Pérez");
        clientRequest.setPhone("5512345678");
        clientRequest.setEmail("juan@email.com");
    }

    @Test
    void findAll_deberiaRegresarClientesSinBusqueda() {

        when(clientRepository.findByActiveTrue(any(Pageable.class)))
                .thenReturn(clientPage);

        when(pageMapper.<Client, ClientResponse>toPageResponse(
                eq(clientPage),
                any(Function.class)))
                .thenReturn(pageResponse);

        PageResponse<ClientResponse> result = clientService.findAll(
                0,
                10,
                "name",
                "asc",
                "");

        assertNotNull(result);

        verify(clientRepository).findByActiveTrue(any(Pageable.class));
        verify(pageMapper).toPageResponse(
                eq(clientPage),
                any(Function.class));
    }

    @Test
    void findAll_deberiaBuscarPorNombre() {

        when(clientRepository.findByNameContainingIgnoreCaseAndActiveTrue(
                eq("Juan"),
                any(Pageable.class)))
                .thenReturn(clientPage);

        when(pageMapper.<Client, ClientResponse>toPageResponse(
                eq(clientPage),
                any(Function.class)))
                .thenReturn(pageResponse);

        PageResponse<ClientResponse> result = clientService.findAll(
                0,
                10,
                "name",
                "asc",
                "Juan");

        assertNotNull(result);

        verify(clientRepository).findByNameContainingIgnoreCaseAndActiveTrue(
                eq("Juan"),
                any(Pageable.class));

        verify(pageMapper).toPageResponse(
                eq(clientPage),
                any(Function.class));
    }

    @Test
    void findById_deberiaRegresarCliente() {

        when(clientRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(client));

        when(clientMapper.toResponse(client))
                .thenReturn(clientResponse);

        ClientResponse result = clientService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan Pérez", result.getName());

        verify(clientRepository).findByIdAndActiveTrue(1L);
        verify(clientMapper).toResponse(client);
    }

    @Test
    void findById_deberiaLanzarExcepcionSiNoExiste() {

        when(clientRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> clientService.findById(1L));

        verify(clientRepository).findByIdAndActiveTrue(1L);
    }

    @Test
    void save_deberiaGuardarCliente() {

        when(clientRepository.findByNameAndActiveTrue("Juan Pérez"))
                .thenReturn(Optional.empty());

        when(clientMapper.toEntity(clientRequest))
                .thenReturn(client);

        when(clientRepository.save(client))
                .thenReturn(client);

        when(clientMapper.toResponse(client))
                .thenReturn(clientResponse);

        ClientResponse result = clientService.save(clientRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan Pérez", result.getName());

        verify(clientRepository).findByNameAndActiveTrue("Juan Pérez");
        verify(clientMapper).toEntity(clientRequest);
        verify(clientRepository).save(client);
        verify(clientMapper).toResponse(client);
    }

    @Test
    void save_deberiaLanzarExcepcionSiClienteYaExiste() {

        when(clientRepository.findByNameAndActiveTrue("Juan Pérez"))
                .thenReturn(Optional.of(client));

        assertThrows(
                DuplicateResourceException.class,
                () -> clientService.save(clientRequest));

        verify(clientRepository).findByNameAndActiveTrue("Juan Pérez");
    }

    @Test
    void update_deberiaActualizarCliente() {

        ClientRequest request = new ClientRequest();
        request.setName("Pedro López");
        request.setPhone("5598765432");
        request.setEmail("pedro@email.com");

        ClientResponse updatedResponse = ClientResponse.builder()
                .id(1L)
                .name("Pedro López")
                .phone("5598765432")
                .email("pedro@email.com")
                .active(true)
                .build();

        when(clientRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(client));

        when(clientRepository.save(client))
                .thenReturn(client);

        when(clientMapper.toResponse(client))
                .thenReturn(updatedResponse);

        ClientResponse result = clientService.update(1L, request);

        assertNotNull(result);
        assertEquals("Pedro López", result.getName());

        verify(clientRepository).findByIdAndActiveTrue(1L);
        verify(clientMapper).updateEntity(client, request);
        verify(clientRepository).save(client);
        verify(clientMapper).toResponse(client);
    }

    @Test
    void update_deberiaLanzarExcepcionSiNuevoNombreYaExiste() {

        ClientRequest request = new ClientRequest();
        request.setName("Pedro López");
        request.setPhone("5598765432");
        request.setEmail("pedro@email.com");

        Client anotherClient = Client.builder()
                .id(2L)
                .name("Pedro López")
                .active(true)
                .build();

        when(clientRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(client));

        when(clientRepository.findByNameAndActiveTrue("Pedro López"))
                .thenReturn(Optional.of(anotherClient));

        assertThrows(
                DuplicateResourceException.class,
                () -> clientService.update(1L, request));

        verify(clientRepository).findByIdAndActiveTrue(1L);
        verify(clientRepository).findByNameAndActiveTrue("Pedro López");
    }

    @Test
    void delete_deberiaDesactivarCliente() {

        when(clientRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(client));

        clientService.delete(1L);

        assertEquals(false, client.getActive());

        verify(clientRepository).findByIdAndActiveTrue(1L);
        verify(clientRepository).save(client);
    }

    @Test
    void delete_deberiaLanzarExcepcionSiNoExiste() {

        when(clientRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> clientService.delete(1L));

        verify(clientRepository).findByIdAndActiveTrue(1L);
    }
}