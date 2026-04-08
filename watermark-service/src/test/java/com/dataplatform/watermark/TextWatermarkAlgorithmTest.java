package com.dataplatform.watermark;

import com.dataplatform.watermark.algorithm.TextWatermarkAlgorithm;
import com.dataplatform.watermark.model.WatermarkData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextWatermarkAlgorithmTest {
    
    private TextWatermarkAlgorithm algorithm;
    
    @BeforeEach
    void setUp() {
        algorithm = new TextWatermarkAlgorithm();
    }
    
    @Test
    void testEmbedAndExtract() {
        String originalText = "This is a test document.";
        WatermarkData watermark = WatermarkData.builder()
                .userId(123L)
                .timestamp(System.currentTimeMillis())
                .dataType("document")
                .build();
        
        String watermarkedText = algorithm.embed(originalText, watermark);
        
        assertNotNull(watermarkedText);
        assertTrue(watermarkedText.length() > originalText.length());
        
        WatermarkData extracted = algorithm.extract(watermarkedText);
        
        assertNotNull(extracted);
        assertEquals(watermark.getUserId(), extracted.getUserId());
        assertEquals(watermark.getDataType(), extracted.getDataType());
    }
    
    @Test
    void testVerifyWithWatermark() {
        String originalText = "Document with watermark";
        WatermarkData watermark = WatermarkData.builder()
                .userId(456L)
                .timestamp(System.currentTimeMillis())
                .dataType("file")
                .build();
        
        String watermarkedText = algorithm.embed(originalText, watermark);
        
        assertTrue(algorithm.verify(watermarkedText));
    }
    
    @Test
    void testVerifyWithoutWatermark() {
        String plainText = "This text has no watermark";
        
        assertFalse(algorithm.verify(plainText));
    }
    
    @Test
    void testExtractWithoutWatermark() {
        String plainText = "No watermark here";
        
        assertNull(algorithm.extract(plainText));
    }
    
    @Test
    void testWatermarkInvisibility() {
        String originalText = "Invisible watermark test";
        WatermarkData watermark = WatermarkData.builder()
                .userId(789L)
                .timestamp(System.currentTimeMillis())
                .dataType("text")
                .build();
        
        String watermarkedText = algorithm.embed(originalText, watermark);
        
        String visiblePart = watermarkedText.replaceAll("[\\u200B-\\u2060]", "");
        assertTrue(visiblePart.startsWith(originalText));
    }
    
    @Test
    void testNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            algorithm.embed(null, WatermarkData.builder().build());
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            algorithm.embed("text", null);
        });
    }
    
    @Test
    void testDifferentDataTypes() {
        String originalText = "Testing different types";
        String[] dataTypes = {"document", "image", "video", "audio"};
        
        for (String dataType : dataTypes) {
            WatermarkData watermark = WatermarkData.builder()
                    .userId(1L)
                    .timestamp(System.currentTimeMillis())
                    .dataType(dataType)
                    .build();
            
            String watermarkedText = algorithm.embed(originalText, watermark);
            WatermarkData extracted = algorithm.extract(watermarkedText);
            
            assertNotNull(extracted);
            assertEquals(dataType, extracted.getDataType());
        }
    }
    
    @Test
    void testLongText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Lorem ipsum dolor sit amet. ");
        }
        String originalText = sb.toString();
        
        WatermarkData watermark = WatermarkData.builder()
                .userId(999L)
                .timestamp(System.currentTimeMillis())
                .dataType("large_text")
                .build();
        
        String watermarkedText = algorithm.embed(originalText, watermark);
        WatermarkData extracted = algorithm.extract(watermarkedText);
        
        assertNotNull(extracted);
        assertEquals(watermark.getUserId(), extracted.getUserId());
        assertEquals(watermark.getDataType(), extracted.getDataType());
    }
}
