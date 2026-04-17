package com.dataplatform.desensitization.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataplatform.desensitization.entity.DesensitizationLog;
import com.dataplatform.desensitization.mapper.DesensitizationLogMapper;
import com.dataplatform.desensitization.model.DesensitizationRule;
import com.dataplatform.desensitization.model.DetectionResult;
import com.dataplatform.desensitization.model.SensitiveType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DesensitizationService {

    private final SensitiveDataDetector detector;
    private final DesensitizationLogMapper desensitizationLogMapper;

    private static final Map<SensitiveType, DesensitizationRule> DEFAULT_RULES = new HashMap<>();

    static {
        DEFAULT_RULES.put(SensitiveType.PHONE, new DesensitizationRule(SensitiveType.PHONE, 3, 4, '*'));
        DEFAULT_RULES.put(SensitiveType.ID_CARD, new DesensitizationRule(SensitiveType.ID_CARD, 6, 3, '*'));
        DEFAULT_RULES.put(SensitiveType.BANK_CARD, new DesensitizationRule(SensitiveType.BANK_CARD, 6, 4, '*'));
        DEFAULT_RULES.put(SensitiveType.EMAIL, new DesensitizationRule(SensitiveType.EMAIL, 1, 0, '*'));
    }

    public String desensitize(String value, SensitiveType type) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String maskedValue;
        switch (type) {
            case PHONE:
                maskedValue = desensitizePhone(value);
                break;
            case ID_CARD:
                maskedValue = desensitizeIdCard(value);
                break;
            case BANK_CARD:
                maskedValue = desensitizeBankCard(value);
                break;
            case EMAIL:
                maskedValue = desensitizeEmail(value);
                break;
            case ADDRESS:
            case NAME:
            default:
                maskedValue = maskAll(value);
        }
        saveLog(type.name(), value, maskedValue, "DESENSITIZE");
        return maskedValue;
    }

    public String desensitizePhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        DesensitizationRule rule = DEFAULT_RULES.get(SensitiveType.PHONE);
        return applyMask(phone, rule.getPrefixLength(), rule.getSuffixLength(), rule.getMaskChar());
    }

    public String desensitizeIdCard(String idCard) {
        if (idCard == null || idCard.length() < 9) {
            return idCard;
        }
        DesensitizationRule rule = DEFAULT_RULES.get(SensitiveType.ID_CARD);
        return applyMask(idCard, rule.getPrefixLength(), rule.getSuffixLength(), rule.getMaskChar());
    }

    public String desensitizeBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 10) {
            return bankCard;
        }
        DesensitizationRule rule = DEFAULT_RULES.get(SensitiveType.BANK_CARD);
        return applyMask(bankCard, rule.getPrefixLength(), rule.getSuffixLength(), rule.getMaskChar());
    }

    public String desensitizeEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);
        
        if (localPart.length() <= 1) {
            return localPart + domainPart;
        }
        
        String maskedLocal;
        if (localPart.length() == 2) {
            maskedLocal = localPart.charAt(0) + "*";
        } else {
            maskedLocal = localPart.charAt(0) + "*" + localPart.charAt(localPart.length() - 1);
        }
        
        return maskedLocal + domainPart;
    }

    public Map<String, Object> autoDesensitize(Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof String) {
                String stringValue = (String) value;
                List<DetectionResult> detections = detector.detectAll(stringValue);
                
                if (detections.isEmpty()) {
                    result.put(key, stringValue);
                } else {
                    String desensitized = stringValue;
                    for (DetectionResult detection : detections) {
                        String masked = desensitize(detection.getValue(), detection.getSensitiveType());
                        desensitized = desensitized.replace(detection.getValue(), masked);
                    }
                    result.put(key, desensitized);
                }
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    private String applyMask(String value, int prefixLength, int suffixLength, char maskChar) {
        if (value == null || value.length() <= prefixLength + suffixLength) {
            return value;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(value.substring(0, prefixLength));
        
        int maskLength = value.length() - prefixLength - suffixLength;
        for (int i = 0; i < maskLength; i++) {
            sb.append(maskChar);
        }
        
        sb.append(value.substring(value.length() - suffixLength));
        
        return sb.toString();
    }

    private String maskAll(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            sb.append('*');
        }
        return sb.toString();
    }

    private void saveLog(String dataType, String originalValue, String maskedValue, String operationType) {
        DesensitizationLog log = new DesensitizationLog();
        log.setDataType(dataType);
        log.setOriginalValue(originalValue);
        log.setMaskedValue(maskedValue);
        log.setOperationType(operationType);
        desensitizationLogMapper.insert(log);
    }

    public List<DesensitizationLog> queryLogs(int page, int size) {
        LambdaQueryWrapper<DesensitizationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DesensitizationLog::getCreatedAt);
        return desensitizationLogMapper.selectList(wrapper);
    }

    public DesensitizationLog getLogById(Long id) {
        return desensitizationLogMapper.selectById(id);
    }

    public List<DesensitizationLog> queryLogsByDataType(String dataType) {
        LambdaQueryWrapper<DesensitizationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DesensitizationLog::getDataType, dataType)
                .orderByDesc(DesensitizationLog::getCreatedAt);
        return desensitizationLogMapper.selectList(wrapper);
    }
}
