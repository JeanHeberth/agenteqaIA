package com.agenteia.agenteqaia.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conversa {

    private String id = UUID.randomUUID().toString();
    private String mensagem;
    private String resposta;
    private LocalDateTime dataHora;
    private String usuario;
}
