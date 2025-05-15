package com.agenteia.agenteqaia.repository;

import com.agenteia.agenteqaia.entity.Conversa;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConversaRepository extends MongoRepository<Conversa, String> {

}
