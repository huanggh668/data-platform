package com.dataplatform.watermark.algorithm;

import com.dataplatform.watermark.model.WatermarkData;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TextWatermarkAlgorithm {
    
    private static final String ZERO_WIDTH_SPACE = "\u200B";
    private static final String ZERO_WIDTH_NON_JOINER = "\u200C";
    private static final String ZERO_WIDTH_JOINER = "\u200D";
    private static final String WORD_JOINER = "\u2060";
    private static final String START_MARKER = "[START]";
    private static final String END_MARKER = "[END]";
    
    private static final char[] ZERO_WIDTH_CHARS = {
        '\u200B', '\u200C', '\u200D', '\u2060'
    };
    
    public String embed(String text, WatermarkData watermark) {
        if (text == null || watermark == null) {
            throw new IllegalArgumentException("Text and watermark data cannot be null");
        }
        
        String encodedData = encodeToBinary(watermark.toEncodedString());
        String watermarkMarker = START_MARKER + encodedData + END_MARKER;
        
        return text + ZERO_WIDTH_SPACE + watermarkMarker;
    }
    
    public WatermarkData extract(String watermarkedText) {
        if (watermarkedText == null) {
            return null;
        }
        
        int startIndex = watermarkedText.indexOf(START_MARKER);
        int endIndex = watermarkedText.indexOf(END_MARKER);
        
        if (startIndex == -1 || endIndex == -1) {
            return null;
        }
        
        int dataStartIndex = startIndex + START_MARKER.length();
        String encodedData = watermarkedText.substring(dataStartIndex, endIndex);
        
        String decodedString = decodeFromBinary(encodedData);
        return WatermarkData.fromEncodedString(decodedString);
    }
    
    public boolean verify(String text) {
        return extract(text) != null;
    }
    
    private String encodeToBinary(String data) {
        String base64 = Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder binaryBuilder = new StringBuilder();
        
        for (char c : base64.toCharArray()) {
            int value = charToValue(c);
            StringBuilder charBinary = new StringBuilder(Integer.toBinaryString(value));
            while (charBinary.length() < 4) {
                charBinary.insert(0, '0');
            }
            for (char bit : charBinary.toString().toCharArray()) {
                binaryBuilder.append(bit == '1' ? ZERO_WIDTH_CHARS[1] : ZERO_WIDTH_CHARS[0]);
            }
        }
        
        return binaryBuilder.toString();
    }
    
    private String decodeFromBinary(String binary) {
        StringBuilder base64Builder = new StringBuilder();
        
        for (int i = 0; i + 4 <= binary.length(); i += 4) {
            String fourBits = "";
            for (int j = 0; j < 4; j++) {
                char c = binary.charAt(i + j);
                fourBits += (c == ZERO_WIDTH_CHARS[1] || c == ZERO_WIDTH_CHARS[2] || c == ZERO_WIDTH_CHARS[3]) ? '1' : '0';
            }
            int value = Integer.parseInt(fourBits, 2);
            base64Builder.append(valueToChar(value));
        }
        
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Builder.toString());
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    private int charToValue(char c) {
        if (c >= 'A' && c <= 'Z') return c - 'A';
        if (c >= 'a' && c <= 'z') return 26 + (c - 'a');
        if (c >= '0' && c <= '9') return 52 + (c - '0');
        if (c == '+') return 62;
        if (c == '/') return 63;
        if (c == '=') return 0;
        return 0;
    }
    
    private char valueToChar(int value) {
        if (value >= 0 && value <= 25) return (char) ('A' + value);
        if (value >= 26 && value <= 51) return (char) ('a' + (value - 26));
        if (value >= 52 && value <= 61) return (char) ('0' + (value - 52));
        if (value == 62) return '+';
        if (value == 63) return '/';
        return '=';
    }
}
