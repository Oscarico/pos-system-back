package com.osulaa.possystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.osulaa.possystem.dto.client.ClientRequest;
import com.osulaa.possystem.dto.client.ClientResponse;
import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.entity.Client;
import com.osulaa.possystem.exception.DuplicateResourceException;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.mapper.ClientMapper;
import com.osulaa.possystem.mapper.PageMapper;
import com.osulaa.possystem.repository.ClientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PageMapper pageMapper;

    public PageResponse<ClientResponse> findAll(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Client> clients;

        if (search == null || search.isBlank()) {
            clients = clientRepository.findByActiveTrue(pageable);
        } else {
            clients = clientRepository.findByNameContainingIgnoreCaseAndActiveTrue(search, pageable);
        }

        return pageMapper.toPageResponse(clients, clientMapper::toResponse);
    }

    public ClientResponse findById(Long id) {
        Client client = getClient(id);

        return clientMapper.toResponse(client);
    }

    public ClientResponse save(ClientRequest request) {
        if (clientRepository.findByNameAndActiveTrue(request.getName()).isPresent()) {
            throw new DuplicateResourceException("El cliente ya existe.");
        }

        Client client = clientMapper.toEntity(request);
        Client saved = clientRepository.save(client);

        return clientMapper.toResponse(saved);
    }

    public ClientResponse update(Long id, ClientRequest request) {
        Client current = getClient(id);

        if (!current.getName().equalsIgnoreCase(request.getName())
                && clientRepository.findByNameAndActiveTrue(request.getName()).isPresent()) {
            throw new DuplicateResourceException("El cliente ya existe.");
        }

        clientMapper.updateEntity(current, request);
        Client updated = clientRepository.save(current);

        return clientMapper.toResponse(updated);
    }

    public void delete(Long id) {
        Client client = getClient(id);

        client.setActive(false);

        clientRepository.save(client);
    }

    private Client getClient(Long id) {
        return clientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado."));
    }
}
