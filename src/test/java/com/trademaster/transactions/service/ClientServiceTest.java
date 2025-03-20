package com.trademaster.transactions.service;

import com.trademaster.transactions.domain.Client;
import com.trademaster.transactions.domain.mapper.ClientMapper;
import com.trademaster.transactions.exception.ResourceBadRequestException;
import com.trademaster.transactions.exception.ResourceNotFoundException;
import com.trademaster.transactions.factory.ClientFactory;
import com.trademaster.transactions.model.ClientCreateDTO;
import com.trademaster.transactions.model.ClientResponseDTO;
import com.trademaster.transactions.repository.ClientRepository;
import com.trademaster.transactions.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {


    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Test
    void testSaveClient_Success() {
        ClientCreateDTO dto = ClientFactory.clientCreateDTO();
        Client client = ClientFactory.client();
        Client savedClient = ClientFactory.client();
        ClientResponseDTO responseDTO = ClientFactory.clientResponseDTO();

        when(clientRepository.findByCpf(dto.getCpf())).thenReturn(Optional.empty());
        when(clientMapper.toEntity(dto)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(savedClient);
        when(clientMapper.toResponseDTO(savedClient)).thenReturn(responseDTO);

        ClientResponseDTO result = clientService.saveClient(dto);

        assertNotNull(result);
        verify(clientRepository).save(client);
    }

    @Test
    void testSaveClient_CpfIsNull() {
        ClientCreateDTO dto = ClientFactory.clientCreateDTO();
        dto.setCpf(null);

        assertThrows(ResourceBadRequestException.class, () -> clientService.saveClient(dto));
    }

    @Test
    void testSaveClient_CpfAlreadyExists() {
        ClientCreateDTO dto = ClientFactory.clientCreateDTO();
        Client existingClient = ClientFactory.client();

        when(clientRepository.findByCpf(dto.getCpf())).thenReturn(Optional.of(existingClient));

        assertThrows(ResourceBadRequestException.class, () -> clientService.saveClient(dto));
    }

    @Test
    void testFindAllClients_Success() {
        Client client1 = ClientFactory.client();
        Client client2 = ClientFactory.client();
        client2.setId(2L);

        ClientResponseDTO responseDTO1 = ClientFactory.clientResponseDTO();
        ClientResponseDTO responseDTO2 = ClientFactory.clientResponseDTO();
        responseDTO2.setId(2L);

        when(clientRepository.findAll()).thenReturn(Arrays.asList(client1, client2));
        when(clientMapper.toResponseDTO(client1)).thenReturn(responseDTO1);
        when(clientMapper.toResponseDTO(client2)).thenReturn(responseDTO2);

        List<ClientResponseDTO> result = clientService.findAllClients();

        assertEquals(2, result.size());
        verify(clientRepository).findAll();
    }

    @Test
    void testFindClientById_Success() {
        Client client = ClientFactory.client();
        ClientResponseDTO responseDTO = ClientFactory.clientResponseDTO();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientMapper.toResponseDTO(client)).thenReturn(responseDTO);

        Optional<ClientResponseDTO> result = clientService.findClientById(1L);

        assertTrue(result.isPresent());
        verify(clientRepository).findById(1L);
    }

    @Test
    void testFindClientEntityById_Success() {
        Client client = ClientFactory.client();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Optional<Client> result = clientService.findClientEntityById(1L);

        assertTrue(result.isPresent());
        verify(clientRepository).findById(1L);
    }

    @Test
    void testUpdateClient_Success() {
        Long id = 1L;
        ClientCreateDTO dto = ClientFactory.clientCreateDTO();
        Client existingClient = ClientFactory.client();
        Client updatedClient = ClientFactory.client();
        ClientResponseDTO responseDTO = ClientFactory.clientResponseDTO();

        when(clientRepository.findById(id)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(existingClient)).thenReturn(updatedClient);
        when(clientMapper.toResponseDTO(updatedClient)).thenReturn(responseDTO);

        ClientResponseDTO result = clientService.updateClient(id, dto);

        assertNotNull(result);
        verify(clientRepository).save(existingClient);
    }

    @Test
    void testUpdateClient_NotFound() {
        Long id = 1L;
        ClientCreateDTO dto = ClientFactory.clientCreateDTO();

        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clientService.updateClient(id, dto));
    }

    @Test
    void testDeleteClient_Success() {
        Long id = 1L;
        doNothing().when(clientRepository).deleteById(id);

        clientService.deleteClient(id);

        verify(clientRepository).deleteById(id);
    }
}
