package com.agenteia.agenteqaia.service;


import com.agenteia.agenteqaia.dto.ConversaRequestDTO;
import com.agenteia.agenteqaia.dto.ConversaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConversaService {

    private final String openaiApiKey = System.getenv("OPENAI_API_KEY");

    public ConversaResponseDTO processarPergunta(ConversaRequestDTO conversaRequestDTO) {
        String resposta = chamarOpenAI(conversaRequestDTO.getMensagem());
        return new ConversaResponseDTO(resposta);
    }

    private String chamarOpenAI(String mensagem) {
        String url = "https://api.openai.com/v1/chat/completions";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", mensagem);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4.1");
        requestBody.put("messages", List.of(message));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> messageResult = (Map<String, Object>) choices.get(0).get("message");
            return messageResult.get("content").toString().trim();
        } catch (Exception e) {
            return "Erro ao consultar a OpenAI: " + e.getMessage();
        }
    }
}
