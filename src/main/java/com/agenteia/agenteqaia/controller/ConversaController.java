package com.agenteia.agenteqaia.controller;


import com.agenteia.agenteqaia.dto.ConversaRequestDTO;
import com.agenteia.agenteqaia.dto.ConversaResponseDTO;
import com.agenteia.agenteqaia.entity.Conversa;
import com.agenteia.agenteqaia.service.ConversaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversa")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ConversaController {

    private final ConversaService conversaService;


    @PostMapping("/pergunta")
    public ConversaResponseDTO perguntar(@RequestBody ConversaRequestDTO request) {
        return conversaService.processarPergunta(request);
    }

    @GetMapping("/resposta")
   public List<ConversaResponseDTO> obterRespostas() {
        return conversaService.listarConversas();
    }
}
