package com.agenteia.agenteqaia.service;

import com.agenteia.agenteqaia.dto.ConversaRequestDTO;
import com.agenteia.agenteqaia.dto.ConversaResponseDTO;
import com.agenteia.agenteqaia.entity.Conversa;
import com.agenteia.agenteqaia.repository.ConversaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversaService {

    private final ConversaRepository conversaRepository;
    private final String openaiApiKey = System.getenv("OPENAI_API_KEY");

    public ConversaResponseDTO processarPergunta(ConversaRequestDTO conversaRequestDTO) {
        String resposta = chamarOpenAI(conversaRequestDTO.getMensagem());

        Conversa conversa = new Conversa();
        conversa.setId(UUID.randomUUID().toString());
        conversa.setMensagem(conversaRequestDTO.getMensagem());
        conversa.setResposta(resposta);
        conversa.setDataHora(LocalDateTime.now());
        conversa.setUsuario("Visitante");

        conversaRepository.save(conversa);

        return new ConversaResponseDTO(
                conversa.getMensagem(),
                conversa.getResposta(),
                conversa.getDataHora(),
                conversa.getUsuario()
        );
    }

    private String chamarOpenAI(String mensagemAtual) {
        String url = "https://api.openai.com/v1/chat/completions";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        List<Conversa> historico = conversaRepository.findByUsuarioOrderByDataHoraAsc("Visitante");

        List<Map<String, String>> mensagens = historico.stream()
                .flatMap(conversa -> List.of(
                        Map.of("role", "user", "content", conversa.getMensagem()),
                        Map.of("role", "assistant", "content", conversa.getResposta())
                ).stream())
                .collect(Collectors.toList());

        mensagens.add(Map.of("role", "user", "content", mensagemAtual));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4");
        requestBody.put("messages", mensagens);

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

    public Page<ConversaResponseDTO> listarConversasPaginado(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataHora"));
        return conversaRepository.findAll(pageRequest)
                .map(conversa -> new ConversaResponseDTO(
                        conversa.getMensagem(),
                        conversa.getResposta(),
                        conversa.getDataHora(),
                        conversa.getUsuario()
                ));
    }

    public List<ConversaResponseDTO> listarConversas() {
        return conversaRepository.findAll(Sort.by(Sort.Direction.DESC, "dataHora")).stream()
                .map(conversa -> new ConversaResponseDTO(
                        conversa.getMensagem(),
                        conversa.getResposta(),
                        conversa.getDataHora(),
                        conversa.getUsuario()
                ))
                .collect(Collectors.toList());
    }
}
