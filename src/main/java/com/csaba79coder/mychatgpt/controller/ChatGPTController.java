package com.csaba79coder.mychatgpt.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequestMapping
@RestController
public class ChatGPTController {

    // https://platform.openai.com/docs/api-reference/images/create-edit

    @Value("${spring.chatgpt.apikey}")
    private String apiKey;

    int maxTokens = 10;

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();


    @GetMapping("/models")
    public ResponseEntity<Object> renderAllModels() {
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        return restTemplate.exchange(
                "https://api.openai.com/v1/models",
                HttpMethod.GET,
                entity,
                Object.class
        );
    }

    @GetMapping("/engines")
    public ResponseEntity<Object> renderAllEnginesForModel() {
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);

        // Replace the MODEL_ID placeholder with the ID of the model you want to retrieve engines for
        // String modelId = "MODEL_ID";
        String modelId = "text-davinci-edit-001";

        // Make a GET request to retrieve the engines for the specified model
        String url = "https://api.openai.com/v1/engines?model=" + modelId;
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Object.class
        );

        return response;
    }

    @GetMapping("/my-chat-gpt-text")
    public ResponseEntity<Object> renderResponseTest() {
        String prompt = "What is the capital of Hungary?";
        String model = "text-davinci-002";
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);
        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("prompt", prompt);
        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Object> response = restTemplate.exchange(
                "https://api.openai.com/v1/completions",
                HttpMethod.POST,
                requestEntity,
                Object.class
        );

        Object responseBody = response.getBody();
        return response;
    }

    @GetMapping("/generate-image")
    public ResponseEntity<byte[]> generateImage() throws IOException {

        // Set request headers
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // Set request body with text prompt and model ID
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "image-alpha-001");
        requestBody.put("prompt", "a cat sitting on a couch");
        requestBody.put("num_images", 1);

        // Send POST request to endpoint
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Return response body as byte array
        return new RestTemplate().exchange(
                "https://api.openai.com/v1/images/generations",
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );
    }
}
