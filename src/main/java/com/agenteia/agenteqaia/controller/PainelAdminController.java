package com.agenteia.agenteqaia.controller;

import com.agenteia.agenteqaia.dto.ConversaResponseDTO;
import com.agenteia.agenteqaia.service.ConversaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/conversas")
@RequiredArgsConstructor
public class PainelAdminController {

    private final ConversaService conversaService;

    @GetMapping
    public Page<ConversaResponseDTO> listarConversasPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return conversaService.listarConversasPaginado(page, size);
    }
}
