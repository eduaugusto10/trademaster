package com.trademaster.transactions.domain.mapper;

import com.trademaster.transactions.domain.Card;
import com.trademaster.transactions.domain.Client;
import com.trademaster.transactions.model.CardResponseDTO;
import com.trademaster.transactions.model.ClientCreateDTO;
import com.trademaster.transactions.model.ClientResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientResponseDTO toResponseDTO(Client client);

    List<ClientResponseDTO> toResponseDTOList(List<Client> clients);

    CardResponseDTO toResponseDTO(Card card);

    Client toEntity(ClientCreateDTO clientDTO);
}

