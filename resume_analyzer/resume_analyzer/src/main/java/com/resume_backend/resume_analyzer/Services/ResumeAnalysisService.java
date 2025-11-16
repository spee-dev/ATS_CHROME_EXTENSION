package com.resume_backend.resume_analyzer.Services;

import com.resume_backend.resume_analyzer.utils.GeminiClient;
import com.resume_backend.resume_analyzer.utils.PromptBuilder;
import org.springframework.stereotype.Service;

@Service
public class ResumeAnalysisService {

    private final GeminiClient geminiClient;

    public ResumeAnalysisService(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    public String analyze(String resumeText, String jobDescription) {
        String prompt = PromptBuilder.buildPrompt(resumeText, jobDescription);
        return geminiClient.callGemini(prompt);
    }
}
