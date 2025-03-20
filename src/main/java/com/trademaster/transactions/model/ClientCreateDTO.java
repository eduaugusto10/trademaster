package com.trademaster.transactions.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClientCreateDTO {

    private String name;
    private String phone;
    private String cpf;
    private List<CardCreateDTO> cards;
}
