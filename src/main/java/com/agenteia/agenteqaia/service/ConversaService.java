package com.agenteia.agenteqaia.service;

import com.agenteia.agenteqaia.dto.ConversaRequestDTO;
import com.agenteia.agenteqaia.dto.ConversaResponseDTO;
import com.agenteia.agenteqaia.entity.Conversa;
import com.agenteia.agenteqaia.repository.ConversaRepository;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversaService {

    private final ConversaRepository conversaRepository;
    @Value("${openai.api.key}")
    private String openaiApiKey;


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

    public String receberArquivo(MultipartFile file) {
        try {
            String nomeArquivo = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destino = Paths.get("/tmp/" + nomeArquivo); // caminho para o arquivo na pasta tmp
            Files.write(destino, file.getBytes());

            // OCR com Tess4J
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load(); // para não falhar no Render

            System.setProperty("jna.library.path", dotenv.get("JNA_LIBRARY_PATH", "/usr/lib"));

            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath(dotenv.get("TESSDATA_PREFIX", "/usr/share/tessdata")); // default do docker
            tesseract.setLanguage(dotenv.get("TESS_LANG", "por"));

            String textoExtraido = tesseract.doOCR(destino.toFile());

            String resposta = chamarOpenAI(textoExtraido);

            Conversa conversa = new Conversa();
            conversa.setId(UUID.randomUUID().toString());
            conversa.setMensagem("Arquivo enviado: " + nomeArquivo);
            conversa.setResposta(resposta);
            conversa.setDataHora(LocalDateTime.now());
            conversa.setUsuario("Visitante");

            conversaRepository.save(conversa);
            return resposta;

        } catch (IOException | TesseractException e) {
            return "Erro ao processar o arquivo: " + e.getMessage();
        }
    }

}
