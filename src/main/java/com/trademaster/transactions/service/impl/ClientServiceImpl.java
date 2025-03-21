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
        if (Objects.isNull(clientDTO.getCpf())) {
            throw new ResourceBadRequestException("Parâmetro CPF é obrigatório");
        }

        clientRepository.findByCpf(clientDTO.getCpf())
                .ifPresent(c -> {
                    throw new ResourceBadRequestException("CPF: " + clientDTO.getCpf() + " cadastrado");
                });

        Client client = clientMapper.toEntity(clientDTO);

        Client savedClient = clientRepository.save(client);
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
        // Verifique se o cliente existe
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        // Atualize os dados do cliente
        existingClient.setName(clientCreateDTO.getName());
        existingClient.setCpf(clientCreateDTO.getCpf());
        existingClient.setPhone(clientCreateDTO.getPhone());

        // Crie uma nova lista de cartões para o cliente
        List<Card> updatedCards = new ArrayList<>();

        // Atualize ou crie novos cartões
        for (CardCreateDTO cardCreateDTO : clientCreateDTO.getCards()) {
            // Procura um cartão existente com o número do cartão, se não existir, cria um novo
            Card card = cardRepository.findByCardNumber(cardCreateDTO.getCardNumber())
                    .orElse(new Card()); // Se não encontrar, cria um novo cartão

            // Atualize os campos do cartão
            card.setCardNumber(cardCreateDTO.getCardNumber());
            card.setType(cardCreateDTO.getType());
            card.setBalance(cardCreateDTO.getBalance());

            // Adicione o cartão à lista de cartões atualizada
            updatedCards.add(card);

            // Salve o cartão no banco de dados
            cardRepository.save(card);
        }

        // Agora, associe os cartões ao cliente sem a necessidade de setClient
        existingClient.setCards(updatedCards);

        // Salve o cliente atualizado
        try {
            clientRepository.save(existingClient);
        }catch (Exception e){
            log.error("Erro ao salvar cliente", e);
        }

        // Retorne o cliente atualizado
        return clientMapper.toResponseDTO(existingClient); // Ou retorne o cliente conforme sua necessidade
    }

    public void deleteClient(Long id) {
        log.info("Deletando cliente: {}", id);
        clientRepository.deleteById(id);
    }
}
