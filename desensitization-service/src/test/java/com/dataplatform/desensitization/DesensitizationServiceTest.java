package com.dataplatform.desensitization;

import com.dataplatform.desensitization.model.SensitiveType;
import com.dataplatform.desensitization.service.DesensitizationService;
import com.dataplatform.desensitization.service.SensitiveDataDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DesensitizationServiceTest {

    private DesensitizationService desensitizationService;
    private SensitiveDataDetector detector;

    @BeforeEach
    void setUp() {
        detector = new SensitiveDataDetector();
        desensitizationService = new DesensitizationService(detector);
    }

    @Test
    void testDesensitizePhone() {
        String result = desensitizationService.desensitizePhone("13812345678");
        assertEquals("138****5678", result);
    }

    @Test
    void testDesensitizePhone_Short() {
        String result = desensitizationService.desensitizePhone("123456");
        assertEquals("123456", result);
    }

    @Test
    void testDesensitizePhone_Null() {
        String result = desensitizationService.desensitizePhone(null);
        assertNull(result);
    }

    @Test
    void testDesensitizeIdCard() {
        String result = desensitizationService.desensitizeIdCard("110101199001011234");
        assertEquals("110101*********234", result);
    }

    @Test
    void testDesensitizeIdCard_Short() {
        String result = desensitizationService.desensitizeIdCard("123456789");
        assertEquals("123456789", result);
    }

    @Test
    void testDesensitizeIdCard_Null() {
        String result = desensitizationService.desensitizeIdCard(null);
        assertNull(result);
    }

    @Test
    void testDesensitizeBankCard() {
        String result = desensitizationService.desensitizeBankCard("6217001234567890123");
        assertEquals("621700*********0123", result);
    }

    @Test
    void testDesensitizeBankCard_Short() {
        String result = desensitizationService.desensitizeBankCard("123456789");
        assertEquals("123456789", result);
    }

    @Test
    void testDesensitizeBankCard_Null() {
        String result = desensitizationService.desensitizeBankCard(null);
        assertNull(result);
    }

    @Test
    void testDesensitizeEmail() {
        String result = desensitizationService.desensitizeEmail("user@example.com");
        assertEquals("u*r@example.com", result);
    }

    @Test
    void testDesensitizeEmail_SingleCharLocal() {
        String result = desensitizationService.desensitizeEmail("a@example.com");
        assertEquals("a@example.com", result);
    }

    @Test
    void testDesensitizeEmail_TwoCharLocal() {
        String result = desensitizationService.desensitizeEmail("ab@example.com");
        assertEquals("a*@example.com", result);
    }

    @Test
    void testDesensitizeEmail_NoAtSymbol() {
        String result = desensitizationService.desensitizeEmail("userexample.com");
        assertEquals("userexample.com", result);
    }

    @Test
    void testDesensitizeEmail_Null() {
        String result = desensitizationService.desensitizeEmail(null);
        assertNull(result);
    }

    @Test
    void testDesensitize_ByType() {
        assertEquals("138****5678", desensitizationService.desensitize("13812345678", SensitiveType.PHONE));
        assertEquals("110101*********234", desensitizationService.desensitize("110101199001011234", SensitiveType.ID_CARD));
        assertEquals("621700*********0123", desensitizationService.desensitize("6217001234567890123", SensitiveType.BANK_CARD));
        assertEquals("u*r@example.com", desensitizationService.desensitize("user@example.com", SensitiveType.EMAIL));
    }

    @Test
    void testDesensitize_NullValue() {
        String result = desensitizationService.desensitize(null, SensitiveType.PHONE);
        assertNull(result);
    }

    @Test
    void testDesensitize_EmptyValue() {
        String result = desensitizationService.desensitize("", SensitiveType.PHONE);
        assertEquals("", result);
    }

    @Test
    void testAutoDesensitize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "张三");
        data.put("phone", "13812345678");
        data.put("email", "zhangsan@example.com");
        data.put("idCard", "110101199001011234");

        Map<String, Object> result = desensitizationService.autoDesensitize(data);

        assertEquals("张三", result.get("name"));
        assertEquals("138****5678", result.get("phone"));
        assertEquals("z*n@example.com", result.get("email"));
        assertEquals("110101*********234", result.get("idCard"));
    }

    @Test
    void testAutoDesensitize_NoSensitiveData() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "李四");
        data.put("city", "北京");

        Map<String, Object> result = desensitizationService.autoDesensitize(data);

        assertEquals("李四", result.get("name"));
        assertEquals("北京", result.get("city"));
    }

    @Test
    void testAutoDesensitize_NullValue() {
        Map<String, Object> data = new HashMap<>();
        data.put("phone", null);

        Map<String, Object> result = desensitizationService.autoDesensitize(data);

        assertNull(result.get("phone"));
    }

    @Test
    void testAutoDesensitize_NonStringValue() {
        Map<String, Object> data = new HashMap<>();
        data.put("age", 25);
        data.put("score", 95.5);

        Map<String, Object> result = desensitizationService.autoDesensitize(data);

        assertEquals(25, result.get("age"));
        assertEquals(95.5, result.get("score"));
    }
}
