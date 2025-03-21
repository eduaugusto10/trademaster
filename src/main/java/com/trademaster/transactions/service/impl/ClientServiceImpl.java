package com.trademaster.transactions.service.impl;

import com.trademaster.transactions.domain.Card;
import com.trademaster.transactions.domain.Client;
import com.trademaster.transactions.domain.mapper.ClientMapper;
import com.trademaster.transactions.exception.ResourceBadRequestException;
import com.trademaster.transactions.exception.ResourceNotFoundException;
import com.trademaster.transactions.model.CardCreateDTO;
import com.trademaster.transactions.model.ClientCreateDTO;
import com.trademaster.transactions.model.ClientResponseDTO;
import com.trademaster.transactions.repository.CardRepository;
import com.trademaster.transactions.repository.ClientRepository;
import com.trademaster.transactions.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final CardRepository cardRepository;

    public ClientResponseDTO saveClient(ClientCreateDTO clientDTO) {
        log.info("Criando cliente: {}", clientDTO);
        validatedCPF(clientDTO);

        Client savedClient = clientRepository.save(clientMapper.toEntity(clientDTO));
        log.info("Cliente criado: {}", savedClient);
        return clientMapper.toResponseDTO(savedClient);
    }


    public List<ClientResponseDTO> findAllClients() {
        log.info("Buscando todos clientes");
        List<Client> clients = clientRepository.findAll();
        return clients.stream()
                .map(clientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<ClientResponseDTO> findClientById(Long id) {
        log.info("Buscando cliente: {}", id);
        return clientRepository.findById(id)
                .map(clientMapper::toResponseDTO);
    }

    @Override
    public Optional<Client> findClientEntityById(Long id) {
        log.info("Buscando cliente: {}", id);
        return clientRepository.findById(id);
    }

    public ClientResponseDTO updateClient(Long id, ClientCreateDTO clientCreateDTO) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        existingClient.setName(clientCreateDTO.getName());
        existingClient.setCpf(clientCreateDTO.getCpf());
        existingClient.setPhone(clientCreateDTO.getPhone());

        clientRepository.save(existingClient);

        return clientMapper.toResponseDTO(existingClient);
    }

    public void deleteClient(Long id) {
        log.info("Deletando cliente: {}", id);
        clientRepository.deleteById(id);
    }

    private void updateCards(ClientCreateDTO clientCreateDTO, Client existingClient) {
        List<Card> updatedCards = new ArrayList<>();
        for (CardCreateDTO cardCreateDTO : clientCreateDTO.getCards()) {
            Card card = cardRepository.findByCardNumber(cardCreateDTO.getCardNumber())
                    .orElse(new Card());
            card.setCardNumber(cardCreateDTO.getCardNumber());
            card.setType(cardCreateDTO.getType());
            card.setBalance(cardCreateDTO.getBalance());

            updatedCards.add(card);

            cardRepository.save(card);
        }
        existingClient.setCards(updatedCards);
    }

    private void validatedCPF(ClientCreateDTO clientDTO) {
        if (Objects.isNull(clientDTO.getCpf())) {
            throw new ResourceBadRequestException("Parâmetro CPF é obrigatório");
        }

        clientRepository.findByCpf(clientDTO.getCpf())
                .ifPresent(c -> {
                    throw new ResourceBadRequestException("CPF: " + clientDTO.getCpf() + " cadastrado");
                });
    }
}
