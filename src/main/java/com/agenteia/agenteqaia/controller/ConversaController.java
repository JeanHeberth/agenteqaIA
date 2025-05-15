package com.agenteia.agenteqaia.controller;


import com.agenteia.agenteqaia.dto.ConversaRequestDTO;
import com.agenteia.agenteqaia.dto.ConversaResponseDTO;
import com.agenteia.agenteqaia.service.ConversaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
