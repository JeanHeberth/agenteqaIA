package com.agenteia.agenteqaia.controller;


import com.agenteia.agenteqaia.dto.ConversaRequestDTO;
import com.agenteia.agenteqaia.dto.ConversaResponseDTO;
import com.agenteia.agenteqaia.service.ConversaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/conversa")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ConversaController {

    private final ConversaService conversaService;


    @PostMapping("/pergunta")
    public ResponseEntity<ConversaResponseDTO> conversar(@RequestBody ConversaRequestDTO dto) {
        ConversaResponseDTO resposta = conversaService.processarPergunta(dto);
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> receberArquivo(@RequestParam("file") MultipartFile arquivo) {
      return ResponseEntity.ok(conversaService.receberArquivo(arquivo));
    }

    @GetMapping("/resposta")
   public List<ConversaResponseDTO> obterRespostas() {
        return conversaService.listarConversas();
    }
}
