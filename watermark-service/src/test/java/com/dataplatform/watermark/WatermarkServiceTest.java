package com.dataplatform.watermark;

import com.dataplatform.watermark.algorithm.ImageWatermarkAlgorithm;
import com.dataplatform.watermark.algorithm.TextWatermarkAlgorithm;
import com.dataplatform.watermark.model.WatermarkData;
import com.dataplatform.watermark.model.WatermarkResult;
import com.dataplatform.watermark.model.WatermarkVerificationResult;
import com.dataplatform.watermark.service.WatermarkService;
import com.dataplatform.watermark.mapper.WatermarkRecordMapper;
import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class WatermarkServiceTest {
    
    private WatermarkService watermarkService;
    
    @BeforeEach
    void setUp() {
        TextWatermarkAlgorithm textAlgorithm = new TextWatermarkAlgorithm();
        ImageWatermarkAlgorithm imageAlgorithm = new ImageWatermarkAlgorithm();
        watermarkService = new WatermarkService(textAlgorithm, imageAlgorithm, Mockito.mock(WatermarkRecordMapper.class));
    }
    
    @Test
    void testEmbedTextWatermark() {
        String text = "Test document content";
        Long userId = 100L;
        String dataType = "test";
        
        WatermarkResult result = watermarkService.embedTextWatermark(text, userId, dataType);
        
        assertNotNull(result);
        assertEquals(text, result.getOriginalData());
        assertNotNull(result.getWatermarkedData());
        assertNotEquals(text, result.getWatermarkedData());
        assertNotNull(result.getWatermarkInfo());
        assertEquals(userId, result.getWatermarkInfo().getUserId());
        assertEquals(dataType, result.getWatermarkInfo().getDataType());
        assertNotNull(result.getTimestamp());
    }
    
    @Test
    void testExtractTextWatermark() {
        String text = "Extract test content";
        Long userId = 200L;
        String dataType = "extract_test";
        
        WatermarkResult embedResult = watermarkService.embedTextWatermark(text, userId, dataType);
        WatermarkData extracted = watermarkService.extractWatermark(embedResult.getWatermarkedData());
        
        assertNotNull(extracted);
        assertEquals(userId, extracted.getUserId());
        assertEquals(dataType, extracted.getDataType());
        assertNotNull(extracted.getTimestamp());
    }
    
    @Test
    void testVerifyTextWatermark() {
        String text = "Verify test content";
        Long userId = 300L;
        String dataType = "verify_test";
        
        WatermarkResult embedResult = watermarkService.embedTextWatermark(text, userId, dataType);
        WatermarkVerificationResult verifyResult = watermarkService.verifyTextWatermark(embedResult.getWatermarkedData());
        
        assertTrue(verifyResult.isValid());
        assertNotNull(verifyResult.getWatermarkData());
        assertEquals(userId, verifyResult.getWatermarkData().getUserId());
    }
    
    @Test
    void testVerifyTextWatermarkFailure() {
        String plainText = "No watermark here";
        
        WatermarkVerificationResult result = watermarkService.verifyTextWatermark(plainText);
        
        assertFalse(result.isValid());
        assertNull(result.getWatermarkData());
        assertNotNull(result.getMessage());
    }
    
    @Test
    void testEmbedImageWatermark() throws IOException {
        byte[] imageData = createTestImage();
        Long userId = 400L;
        String dataType = "image_test";
        
        WatermarkResult result = watermarkService.embedImageWatermark(imageData, userId, dataType);
        
        assertNotNull(result);
        assertNotNull(result.getWatermarkedData());
        assertNotEquals(result.getOriginalData(), result.getWatermarkedData());
        assertNotNull(result.getWatermarkInfo());
        assertEquals(userId, result.getWatermarkInfo().getUserId());
        assertEquals(dataType, result.getWatermarkInfo().getDataType());
    }
    
    @Test
    void testExtractImageWatermark() throws IOException {
        byte[] imageData = createTestImage();
        Long userId = 500L;
        String dataType = "image_extract";
        
        WatermarkResult embedResult = watermarkService.embedImageWatermark(imageData, userId, dataType);
        byte[] watermarkedImageBytes = java.util.Base64.getDecoder().decode(embedResult.getWatermarkedData());
        
        WatermarkData extracted = watermarkService.extractImageWatermark(watermarkedImageBytes);
        
        assertNotNull(extracted);
        assertEquals(userId, extracted.getUserId());
        assertEquals(dataType, extracted.getDataType());
    }
    
    @Test
    void testVerifyImageWatermark() throws IOException {
        byte[] imageData = createTestImage();
        Long userId = 600L;
        String dataType = "image_verify";
        
        WatermarkResult embedResult = watermarkService.embedImageWatermark(imageData, userId, dataType);
        byte[] watermarkedImageBytes = java.util.Base64.getDecoder().decode(embedResult.getWatermarkedData());
        
        WatermarkVerificationResult result = watermarkService.verifyImageWatermark(watermarkedImageBytes);
        
        assertTrue(result.isValid());
        assertNotNull(result.getWatermarkData());
        assertEquals(userId, result.getWatermarkData().getUserId());
    }
    
    @Test
    void testVerifyImageWatermarkFailure() throws IOException {
        byte[] imageData = createTestImage();
        
        WatermarkVerificationResult result = watermarkService.verifyImageWatermark(imageData);
        
        assertFalse(result.isValid());
    }
    
    private byte[] createTestImage() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                image.setRGB(x, y, (x << 16) | (y << 8) | 0xFF);
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
