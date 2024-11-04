package br.com.joaopfsiqueira.pedidos.notificacao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Produto {

    private UUID id = UUID.randomUUID();
    private String nome;
    private Double valor;
}
