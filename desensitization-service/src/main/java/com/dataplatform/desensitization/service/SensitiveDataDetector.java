package com.dataplatform.desensitization.service;

import com.dataplatform.desensitization.model.DetectionResult;
import com.dataplatform.desensitization.model.SensitiveType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SensitiveDataDetector {

    private static final Pattern PHONE_PATTERN = Pattern.compile("(?<!\\d)(1[3-9]\\d{9})(?!\\d)");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(?<!\\d)([1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx])(?!\\d)");
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("(?<!\\d)(\\d{16,19})(?!\\d)");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");

    public List<DetectionResult> detectPhone(String text) {
        List<DetectionResult> results = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return results;
        }
        Matcher matcher = PHONE_PATTERN.matcher(text);
        while (matcher.find()) {
            DetectionResult result = new DetectionResult();
            result.setFieldName("phone");
            result.setValue(matcher.group(1));
            result.setSensitiveType(SensitiveType.PHONE);
            result.setStartIndex(matcher.start());
            result.setEndIndex(matcher.end());
            results.add(result);
        }
        return results;
    }

    public List<DetectionResult> detectIdCard(String text) {
        List<DetectionResult> results = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return results;
        }
        Matcher matcher = ID_CARD_PATTERN.matcher(text);
        while (matcher.find()) {
            DetectionResult result = new DetectionResult();
            result.setFieldName("idCard");
            result.setValue(matcher.group(1));
            result.setSensitiveType(SensitiveType.ID_CARD);
            result.setStartIndex(matcher.start());
            result.setEndIndex(matcher.end());
            results.add(result);
        }
        return results;
    }

    public List<DetectionResult> detectBankCard(String text) {
        List<DetectionResult> results = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return results;
        }
        Matcher matcher = BANK_CARD_PATTERN.matcher(text);
        while (matcher.find()) {
            String cardNumber = matcher.group(1);
            if (isValidLuhn(cardNumber)) {
                DetectionResult result = new DetectionResult();
                result.setFieldName("bankCard");
                result.setValue(cardNumber);
                result.setSensitiveType(SensitiveType.BANK_CARD);
                result.setStartIndex(matcher.start());
                result.setEndIndex(matcher.end());
                results.add(result);
            }
        }
        return results;
    }

    public List<DetectionResult> detectEmail(String text) {
        List<DetectionResult> results = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return results;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        while (matcher.find()) {
            DetectionResult result = new DetectionResult();
            result.setFieldName("email");
            result.setValue(matcher.group(1));
            result.setSensitiveType(SensitiveType.EMAIL);
            result.setStartIndex(matcher.start());
            result.setEndIndex(matcher.end());
            results.add(result);
        }
        return results;
    }

    public List<DetectionResult> detectAll(String text) {
        List<DetectionResult> results = new ArrayList<>();
        results.addAll(detectPhone(text));
        results.addAll(detectIdCard(text));
        results.addAll(detectBankCard(text));
        results.addAll(detectEmail(text));
        return results;
    }

    private boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            sum += digit;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}
