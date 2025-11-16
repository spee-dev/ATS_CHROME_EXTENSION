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

@Component
public class GeminiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    public String callGemini(String prompt) {
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

            HttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(
                    "https://generativelanguage.googleapis.com/v1beta/models/"
                            + "gemini-2.5-flash:generateContent?key=" + apiKey
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

        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for detailed error
            return "Error calling Gemini: " + e.getMessage();
        }
    }
}
