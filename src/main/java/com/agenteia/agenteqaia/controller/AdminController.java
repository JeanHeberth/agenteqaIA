package com.agenteia.agenteqaia.controller;

import com.agenteia.agenteqaia.service.ConversaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final ConversaService conversaService;

    @GetMapping("/admin")
    public String painelAdmin(Model model) {
        model.addAttribute("conversas", conversaService.listarConversas());
        return "admin";
    }
}
