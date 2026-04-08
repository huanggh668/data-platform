package com.dataplatform.watermark.algorithm;

import com.dataplatform.watermark.model.WatermarkData;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class ImageWatermarkAlgorithm {
    
    private static final String MAGIC_HEADER = "WMS";
    private static final int MAGIC_HEADER_BITS = 24;
    private static final int LENGTH_BITS = 32;
    private static final int CHECKSUM_BITS = 32;
    
    public byte[] embed(byte[] imageData, WatermarkData watermark) throws IOException {
        if (imageData == null || watermark == null) {
            throw new IllegalArgumentException("Image data and watermark cannot be null");
        }
        
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        if (image == null) {
            throw new IllegalArgumentException("Invalid image format");
        }
        
        String watermarkString = MAGIC_HEADER + watermark.toEncodedString();
        byte[] watermarkBytes = watermarkString.getBytes(StandardCharsets.UTF_8);
        byte[] lengthBytes = intToBytes(watermarkBytes.length);
        byte[] checksumBytes = intToBytes(calculateChecksum(watermarkBytes));
        
        byte[] allData = concatenateBytes(lengthBytes, concatenateBytes(checksumBytes, watermarkBytes));
        
        int width = image.getWidth();
        int height = image.getHeight();
        int maxBits = width * height * 3;
        int requiredBits = MAGIC_HEADER_BITS + LENGTH_BITS + CHECKSUM_BITS + (allData.length * 8);
        
        if (requiredBits > maxBits) {
            throw new IllegalArgumentException("Image too small to embed watermark");
        }
        
        BufferedImage resultImage = new BufferedImage(width, height, image.getType());
        
        int bitIndex = 0;
        boolean[] bits = bytesToBits(allData);
        
        outer:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 0) & 0xFF;
                
                if (bitIndex < bits.length) {
                    r = (r & 0xFE) | (bits[bitIndex++] ? 1 : 0);
                }
                if (bitIndex < bits.length) {
                    g = (g & 0xFE) | (bits[bitIndex++] ? 1 : 0);
                }
                if (bitIndex < bits.length) {
                    b = (b & 0xFE) | (bits[bitIndex++] ? 1 : 0);
                }
                
                int newRgb = (0xFF << 24) | (r << 16) | (g << 8) | b;
                resultImage.setRGB(x, y, newRgb);
                
                if (bitIndex >= bits.length) {
                    break outer;
                }
            }
        }
        
        for (int y = height - 1; y >= 0 && bitIndex < bits.length; y--) {
            for (int x = width - 1; x >= 0 && bitIndex < bits.length; x--) {
                int rgb = resultImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 0) & 0xFF;
                
                if (bitIndex < bits.length) {
                    r = (r & 0xFE) | (bits[bitIndex++] ? 1 : 0);
                }
                if (bitIndex < bits.length) {
                    g = (g & 0xFE) | (bits[bitIndex++] ? 1 : 0);
                }
                if (bitIndex < bits.length) {
                    b = (b & 0xFE) | (bits[bitIndex++] ? 1 : 0);
                }
                
                int newRgb = (0xFF << 24) | (r << 16) | (g << 8) | b;
                resultImage.setRGB(x, y, newRgb);
            }
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String format = detectImageFormat(imageData);
        ImageIO.write(resultImage, format, baos);
        return baos.toByteArray();
    }
    
    public WatermarkData extract(byte[] watermarkedImage) throws IOException {
        if (watermarkedImage == null) {
            return null;
        }
        
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(watermarkedImage));
        if (image == null) {
            return null;
        }
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        int totalBits = width * height * 3;
        boolean[] bits = new boolean[totalBits];
        
        int bitIndex = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                bits[bitIndex++] = ((rgb >> 16) & 1) == 1;
                bits[bitIndex++] = ((rgb >> 8) & 1) == 1;
                bits[bitIndex++] = ((rgb >> 0) & 1) == 1;
            }
        }
        
        if (bitIndex < MAGIC_HEADER_BITS + LENGTH_BITS + CHECKSUM_BITS + 8) {
            return null;
        }
        
        byte[] lengthBytes = bitsToBytes(bits, 0, LENGTH_BITS);
        int dataLength = bytesToInt(lengthBytes);
        
        if (dataLength <= 0 || dataLength > 10000) {
            return null;
        }
        
        int totalRequired = MAGIC_HEADER_BITS + LENGTH_BITS + CHECKSUM_BITS + (dataLength * 8);
        if (bitIndex < totalRequired) {
            return null;
        }
        
        byte[] checksumBytes = bitsToBytes(bits, MAGIC_HEADER_BITS, CHECKSUM_BITS);
        int expectedChecksum = bytesToInt(checksumBytes);
        
        int dataStart = MAGIC_HEADER_BITS + LENGTH_BITS + CHECKSUM_BITS;
        byte[] dataBytes = bitsToBytes(bits, dataStart, dataLength * 8);
        
        if (calculateChecksum(dataBytes) != expectedChecksum) {
            return null;
        }
        
        String watermarkString = new String(dataBytes, StandardCharsets.UTF_8);
        
        if (!watermarkString.startsWith(MAGIC_HEADER)) {
            return null;
        }
        
        String encodedData = watermarkString.substring(MAGIC_HEADER.length());
        return WatermarkData.fromEncodedString(encodedData);
    }
    
    public boolean verify(byte[] image) {
        try {
            return extract(image) != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    private String detectImageFormat(byte[] imageData) {
        if (imageData.length >= 3) {
            if (imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8) {
                return "jpg";
            }
            if (imageData[0] == (byte) 0x89 && imageData[1] == (byte) 0x50) {
                return "png";
            }
        }
        return "png";
    }
    
    private byte[] intToBytes(int value) {
        return new byte[] {
            (byte) ((value >> 24) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) (value & 0xFF)
        };
    }
    
    private int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
               ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) << 8) |
               (bytes[3] & 0xFF);
    }
    
    private int calculateChecksum(byte[] data) {
        int checksum = 0;
        for (byte b : data) {
            checksum = (checksum * 31 + (b & 0xFF)) & 0xFFFFFFFF;
        }
        return checksum;
    }
    
    private boolean[] bytesToBits(byte[] bytes) {
        boolean[] bits = new boolean[bytes.length * 8];
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < 8; j++) {
                bits[i * 8 + j] = ((bytes[i] >> (7 - j)) & 1) == 1;
            }
        }
        return bits;
    }
    
    private byte[] bitsToBytes(boolean[] bits, int offset, int length) {
        byte[] bytes = new byte[length / 8];
        for (int i = 0; i < length; i++) {
            if (bits[offset + i]) {
                bytes[i / 8] |= (1 << (7 - (i % 8)));
            }
        }
        return bytes;
    }
    
    private byte[] concatenateBytes(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
