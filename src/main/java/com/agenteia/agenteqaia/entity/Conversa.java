package com.agenteia.agenteqaia.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "conversas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conversa {

    @Id
    private String id = UUID.randomUUID().toString();
    private String mensagem;
    private String resposta;
    private LocalDateTime dataHora;
    private String usuario;
}
