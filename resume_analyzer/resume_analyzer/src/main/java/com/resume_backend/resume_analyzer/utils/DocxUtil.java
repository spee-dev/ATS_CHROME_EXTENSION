package com.resume_backend.resume_analyzer.utils;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.InputStream;

public class DocxUtil {

    public static String extractText(InputStream inputStream) {
        try (XWPFDocument doc = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {

            return extractor.getText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DOCX", e);
        }
    }
}
