package com.resume_backend.resume_analyzer.Services;

import com.resume_backend.resume_analyzer.utils.GeminiClient;
import com.resume_backend.resume_analyzer.utils.PromptBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; // For thread-safe storage

@Service
public class ResumeAnalysisService {

    private final GeminiClient geminiClient;
    private String storedResumeText;
    private String storedJobDescription;
    private final Map<String, String> chatContext = new ConcurrentHashMap<>(); // To store context for chat

    public ResumeAnalysisService(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    public String analyze(String resumeText, String jobDescription) {
        this.storedResumeText = resumeText;
        this.storedJobDescription = jobDescription;
        String prompt = PromptBuilder.buildPrompt(resumeText, jobDescription);
        return geminiClient.callGemini(prompt);
    }

   public String chat(List<Map<String, String>> messages) {

    if (storedResumeText != null && storedJobDescription != null) {

        String systemInstruction =
                "You are an advanced AI assistant specialized in:\n" +
                "- Resume analysis and ATS optimization\n" +
                "- Job description comparison\n" +
                "- Resume keyword extraction\n" +
                "- Writing quantified resume bullet points\n" +
                "- Improving projects and experience descriptions\n" +
                "- Suggesting missing skills and technologies\n" +
                "- Giving career guidance for students and job seekers\n" +
                "- Explaining technical concepts clearly (e.g., load balancers, microservices)\n\n" +

                "IMPORTANT RULES:\n" +
                "1. NEVER say: 'I'm ready', 'How can I help?', or ask the user what they want.\n" +
                "2. ALWAYS answer the user's question directly.\n" +
                "3. If the question is about the resume or job description → use ATS logic.\n" +
                "4. If the question is technical (e.g., what is API, load balancer?) → answer like a top technical tutor.\n" +
                "5. If the question is general (career, skill learning, interview prep) → give practical, actionable guidance.\n" +
                "6. When rewriting resume bullets → ALWAYS quantify using numbers (%, reduced, increased, optimized).\n" +
                "7. Output must be plain English, no emojis, no markdown.\n\n" +

                "RESUME PROVIDED:\n" + storedResumeText + "\n\n" +
                "JOB DESCRIPTION PROVIDED:\n" + storedJobDescription + "\n\n" +
                "Use the resume & JD only when relevant. Otherwise answer normally.";

        // Insert system instruction as artificial first message
        Map<String, String> systemMsg = new ConcurrentHashMap<>();
        systemMsg.put("sender", "user"); // Gemini uses 'user' as system role
        systemMsg.put("text", systemInstruction);

        messages.add(0, systemMsg);
    }

    return geminiClient.chatWithGemini(messages);
}
}