package com.resume_backend.resume_analyzer.Controller;

import com.resume_backend.resume_analyzer.Services.ResumeAnalysisService;
import com.resume_backend.resume_analyzer.utils.PdfUtil;
import com.resume_backend.resume_analyzer.utils.DocxUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin // optional (global config already present)
public class ResumeController {

    private final ResumeAnalysisService analysisService;

    public ResumeController(ResumeAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jd") String jobDescription
    ) throws Exception {

        String extractedText;
        String fileName = file.getOriginalFilename().toLowerCase();

        if (fileName.endsWith(".pdf")) {
            extractedText = PdfUtil.extractText(file.getInputStream());
        } else if (fileName.endsWith(".docx")) {
            extractedText = DocxUtil.extractText(file.getInputStream());
        } else {
            return ResponseEntity.badRequest().body("Invalid file type!");
        }

        String result = analysisService.analyze(extractedText, jobDescription);
        return ResponseEntity.ok(result);
    }
}
