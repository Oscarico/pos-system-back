package com.osulaa.possystem.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.osulaa.possystem.dto.client.ClientRequest;
import com.osulaa.possystem.dto.client.ClientResponse;
import com.osulaa.possystem.entity.Client;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequest request) {
        return Client.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();
    }

    public ClientResponse toResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .phone(client.getPhone())
                .email(client.getEmail())
                .active(client.getActive())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }

    public void updateEntity(Client client, ClientRequest request) {
        client.setName(request.getName());
        client.setPhone(request.getPhone());
        client.setEmail(request.getEmail());
    }

    public List<ClientResponse> toResponseList(List<Client> clients) {
        return clients.stream()
                .map(this::toResponse)
                .toList();
    }
}
