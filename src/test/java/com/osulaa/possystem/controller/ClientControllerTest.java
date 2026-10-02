package com.osulaa.possystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osulaa.possystem.dto.client.ClientRequest;
import com.osulaa.possystem.dto.client.ClientResponse;
import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.security.CustomUserDetailsService;
import com.osulaa.possystem.security.JwtService;
import com.osulaa.possystem.service.ClientService;

@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean // 👈 Necesario para resolver la dependencia en JwtAuthenticationFilter
    private CustomUserDetailsService customUserDetailsService;

    private ClientRequest clientRequest;
    private ClientResponse clientResponse;

    @BeforeEach
    void setUp() {
        clientRequest = new ClientRequest();
        clientRequest.setName("Juan Perez");
        clientRequest.setPhone("5512345678");
        clientRequest.setEmail("juan@test.com");

        clientResponse = ClientResponse.builder()
                .id(1L)
                .name("Juan Perez")
                .phone("5512345678")
                .email("juan@test.com")
                .active(true)
                .build();
    }

    @Test
    void findAll_deberiaRegresarListaDeClientes() throws Exception {
        PageResponse<ClientResponse> pageResponse = PageResponse.<ClientResponse>builder()
                .items(List.of(clientResponse))
                .page(0)
                .size(10)
                .totalItems(1L)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(clientService.findAll(
                0,
                10,
                "name",
                "asc",
                null))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.items[0].id").value(1))
                .andExpect(jsonPath("$.data.items[0].name").value("Juan Perez"))
                .andExpect(jsonPath("$.data.items[0].phone").value("5512345678"))
                .andExpect(jsonPath("$.data.items[0].email").value("juan@test.com"));

        verify(clientService).findAll(
                0,
                10,
                "name",
                "asc",
                null);
    }

    @Test
    void findAll_deberiaAceptarParametrosDeBusqueda() throws Exception {
        PageResponse<ClientResponse> pageResponse = PageResponse.<ClientResponse>builder()
                .items(List.of(clientResponse))
                .page(1)
                .size(20)
                .totalItems(1L)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(clientService.findAll(
                1,
                20,
                "email",
                "desc",
                "juan"))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/clients")
                .param("page", "1")
                .param("size", "20")
                .param("sortBy", "email")
                .param("sortDir", "desc")
                .param("search", "juan"))
                .andExpect(status().isOk());

        verify(clientService).findAll(
                1,
                20,
                "email",
                "desc",
                "juan");
    }

    @Test
    void getById_deberiaRegresarCliente() throws Exception {
        when(clientService.findById(1L))
                .thenReturn(clientResponse);

        mockMvc.perform(get("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Juan Perez"))
                .andExpect(jsonPath("$.data.phone").value("5512345678"))
                .andExpect(jsonPath("$.data.email").value("juan@test.com"));

        verify(clientService).findById(1L);
    }

    @Test
    void save_deberiaCrearCliente() throws Exception {
        when(clientService.save(any(ClientRequest.class)))
                .thenReturn(clientResponse);

        mockMvc.perform(post("/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientRequest)))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Juan Perez"))
                .andExpect(jsonPath("$.data.email").value("juan@test.com"));

        verify(clientService).save(any(ClientRequest.class));
    }

    @Test
    void update_deberiaActualizarCliente() throws Exception {
        when(clientService.update(
                eq(1L),
                any(ClientRequest.class)))
                .thenReturn(clientResponse);

        mockMvc.perform(put("/clients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientRequest)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Juan Perez"))
                .andExpect(jsonPath("$.data.email").value("juan@test.com"));

        verify(clientService).update(
                eq(1L),
                any(ClientRequest.class));
    }

    @Test
    void delete_deberiaEliminarCliente() throws Exception {
        mockMvc.perform(delete("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()));

        verify(clientService).delete(1L);
    }

    @Test
    void save_deberiaRechazarNombreVacio() throws Exception {
        ClientRequest invalidRequest = new ClientRequest();
        invalidRequest.setName("");
        invalidRequest.setPhone("5512345678");
        invalidRequest.setEmail("juan@test.com");

        mockMvc.perform(post("/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}