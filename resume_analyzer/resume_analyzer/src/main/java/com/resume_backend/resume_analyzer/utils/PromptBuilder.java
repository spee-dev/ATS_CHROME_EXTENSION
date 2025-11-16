package com.resume_backend.resume_analyzer.utils;

public class PromptBuilder {

    public static String buildPrompt(String resume, String jd) {
        return """
            You are an ATS (Applicant Tracking System) resume analyzer.
            Compare the RESUME and JOB DESCRIPTION and return the results in clean,
            plain text only. Do not include any markdown symbols, bold text, emojis,
            special characters, or formatting like **, ##, *, _, ~.

            Strictly follow this output format:

            1. ATS Score (0-100):
               - Provide a single numeric score.

            2. Missing Keywords:
               - Provide a bullet list using hyphens only.
               Example:
               - keyword1
               - keyword2

            3. Skills Match Percentage:
               - Provide a percentage value.
               - Add 2-3 bullet points describing the match.

            4. Weakness Summary:
               - Bullet points describing weaknesses.

            5. Recommendations:
               - Write 5 to 8 bullet points suggesting improvements.
               - Use simple plain text bullets with hyphens.

            6. Final ATS Optimization Score (0-100):
               - Provide a numeric score.

            Do not include any formatting symbols, emoji, or markdown. 
            Only plain English sentences and bullet points using hyphens.

            RESUME:
            %s

            JOB DESCRIPTION:
            %s
        """.formatted(resume, jd);
    }

}
