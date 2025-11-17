package com.resume_backend.resume_analyzer.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GeminiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    public String callGemini(String prompt) {
        // This method can be kept for single-turn interactions if needed, or refactored to use chatWithGemini
        // For now, let's keep it as is and add a new chat method.
        try {
            String escapedPrompt = mapper.writeValueAsString(prompt);

            String jsonBody = """
                {
                  "contents": [
                    {
                      "parts": [{ "text": %s }]
                    }
                  ]
                }
            """.formatted(escapedPrompt);

            return executeGeminiRequest(jsonBody);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling Gemini: " + e.getMessage();
        }
    }

    public String chatWithGemini(List<Map<String, String>> messages) {
        try {
            // Map frontend messages to Gemini API format
            List<Map<String, Object>> contents = messages.stream().map(msg -> {
                String sender = msg.getOrDefault("sender", "user"); // Get sender from the incoming message
                String role = sender.equals("user") ? "user" : "model"; // Map sender to Gemini role
                String text = msg.getOrDefault("text", ""); // Get text from the incoming message
                return Map.of(
                        "role", role,
                        "parts", List.of(Map.of("text", text))
                );
            }).collect(Collectors.toList());

            String jsonBody = mapper.writeValueAsString(Map.of("contents", contents));

            return executeGeminiRequest(jsonBody);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error chatting with Gemini: " + e.getMessage();
        }
    }

    private String executeGeminiRequest(String jsonBody) throws Exception {
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + "gemini-2.0-flash:generateContent?key=" + apiKey
        );

        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(jsonBody));

        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(post);
        String responseString = EntityUtils.toString(response.getEntity());
        System.out.println("Gemini API Raw Response: " + responseString); // Log raw response for debugging

        JsonNode root = mapper.readTree(responseString);
        if (root.has("candidates") && root.get("candidates").isArray() && root.get("candidates").size() > 0) {
            return root.get("candidates").get(0).get("content")
                    .get("parts").get(0).get("text").asText();
        } else if (root.has("error")) {
            return "Error from Gemini API: " + root.get("error").get("message").asText();
        } else {
            return "Unexpected Gemini API response: " + responseString;
        }
    }
}
