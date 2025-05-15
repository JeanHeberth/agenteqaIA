package com.agenteia.agenteqaia.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversaResponseDTO {

    private String mensagem;
    private String resposta;
    private LocalDateTime dataHora;
    private String usuario;
}
