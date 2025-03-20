package com.trademaster.transactions.service;

import com.trademaster.transactions.domain.Client;
import com.trademaster.transactions.model.ClientCreateDTO;
import com.trademaster.transactions.model.ClientResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    Optional<ClientResponseDTO> findClientById(Long id);

    Optional<Client> findClientEntityById(Long id);

    List<ClientResponseDTO> findAllClients();

    ClientResponseDTO saveClient(ClientCreateDTO clientCreateDTO);

    void deleteClient(Long id);

    ClientResponseDTO updateClient(Long id, ClientCreateDTO updatedClientCreateDTO);
}
