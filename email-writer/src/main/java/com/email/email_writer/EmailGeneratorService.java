package com.email.email_writer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class EmailGeneratorService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmailReply(EmailRequest emailRequest){
        //Build the prompt
        String prompt=buildPrompt(emailRequest);
        //Craft a request
        HashMap<String , Object>textPart=new HashMap<>();
        textPart.put("text",prompt);

        List<Object>partsList=new ArrayList<>();
        partsList.add(textPart);
        HashMap<String,Object>partsMap=new HashMap<>();
        partsMap.put("parts",partsList);

        List<Object>contentList=new ArrayList<>();
        contentList.add(partsMap);

        HashMap<String,Object>requestBody=new HashMap<>();
        requestBody.put("contents",contentList);

        //Do request and get response
        Mono<String> response = webClient.post()
                .uri(geminiApiUrl)
                .header("x-goog-api-key", geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);

        // Convert Mono<String> → String
        String responseJson = response.block();


        //Extract response and Return response
        return extractResponseContent(responseJson);
    }

    private String extractResponseContent(String responseJson) {
        try {
            // Step 1: Create ObjectMapper
            ObjectMapper mapper = new ObjectMapper();

            // Step 2: Parse JSON string into tree
            JsonNode root = mapper.readTree(responseJson);

            // Step 3: Navigate to candidates[0].content.parts[0].text
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }

            // Fallback if nothing found
            return "No text found in response";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing response";
        }
    }

    private String buildPrompt(EmailRequest emailRequest){
        StringBuilder prompt=new StringBuilder();
        prompt.append("Generate a professional email reply for the following content. Please don't generate a subject line");
        if(emailRequest.getTone()!=null && !emailRequest.getTone().isEmpty()){
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone");
        }
        prompt.append("\nOriginal Email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}




