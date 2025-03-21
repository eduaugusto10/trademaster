package com.trademaster.transactions.controller;

import com.trademaster.transactions.model.ClientCreateDTO;
import com.trademaster.transactions.model.ClientResponseDTO;
import com.trademaster.transactions.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientCreateDTO clientCreateDTO) {
        ClientResponseDTO savedClient = clientService.saveClient(clientCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> findAllClients() {

        return ResponseEntity.ok(clientService.findAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable Long id) {
        return clientService.findClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable Long id,@Valid @RequestBody ClientCreateDTO clientCreateDTO) {
        return ResponseEntity.ok(clientService.updateClient(id, clientCreateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
