package com.trademaster.transactions.model;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClientCreateDTO {

    private String name;
    private String phone;
    private String cpf;
    @Valid
    private List<CardCreateDTO> cards;
}
