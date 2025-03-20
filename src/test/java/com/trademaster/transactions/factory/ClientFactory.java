package com.trademaster.transactions.factory;

import com.trademaster.transactions.domain.Card;
import com.trademaster.transactions.domain.Client;
import com.trademaster.transactions.model.CardCreateDTO;
import com.trademaster.transactions.model.CardResponseDTO;
import com.trademaster.transactions.model.ClientCreateDTO;
import com.trademaster.transactions.model.ClientResponseDTO;

import java.util.Collections;
import java.util.List;

public class ClientFactory {

    public static ClientCreateDTO clientCreateDTO(){
        List<CardCreateDTO> cardList = Collections.singletonList(CardFactory.cardCreateDTO());
        return ClientCreateDTO.builder()
                .cpf("123456789")
                .name("Eduardo Oliveira")
                .phone("123456789")
                .cards(cardList)
                .build();
    }
    public static ClientResponseDTO clientResponseDTO(){
        List<CardResponseDTO> card = Collections.singletonList(CardFactory.cardResponseDTO());
        return ClientResponseDTO.builder()
                .id(1L)
                .cards(card)
                .cpf("1234567890")
                .phone("123456789")
                .build();
    }
    public static Client client(){
        List<Card> cardList = Collections.singletonList(CardFactory.card());
        return Client.builder()
                .id(1L)
                .cpf("123456789")
                .name("Eduardo Oliveira")
                .phone("123456789")
                .cards(cardList)
                .build();
    }
}
