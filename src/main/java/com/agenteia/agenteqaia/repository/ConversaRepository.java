package com.agenteia.agenteqaia.repository;

import com.agenteia.agenteqaia.entity.Conversa;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConversaRepository extends MongoRepository<Conversa, String> {

    List<Conversa> findByUsuarioOrderByDataHoraAsc(String visitante);
}
