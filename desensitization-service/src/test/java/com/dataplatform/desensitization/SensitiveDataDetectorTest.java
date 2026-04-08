package com.dataplatform.desensitization;

import com.dataplatform.desensitization.model.DetectionResult;
import com.dataplatform.desensitization.model.SensitiveType;
import com.dataplatform.desensitization.service.SensitiveDataDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SensitiveDataDetectorTest {

    private SensitiveDataDetector detector;

    @BeforeEach
    void setUp() {
        detector = new SensitiveDataDetector();
    }

    @Test
    void testDetectPhone_ValidPhone() {
        List<DetectionResult> results = detector.detectPhone("联系电话：13812345678");
        assertEquals(1, results.size());
        assertEquals("13812345678", results.get(0).getValue());
        assertEquals(SensitiveType.PHONE, results.get(0).getSensitiveType());
        assertEquals("phone", results.get(0).getFieldName());
    }

    @Test
    void testDetectPhone_MultiplePhones() {
        List<DetectionResult> results = detector.detectPhone("手机1：13812345678，手机2：13998765432");
        assertEquals(2, results.size());
    }

    @Test
    void testDetectPhone_InvalidPhone() {
        List<DetectionResult> results = detector.detectPhone("号码：12345678901");
        assertEquals(0, results.size());
    }

    @Test
    void testDetectPhone_NullInput() {
        List<DetectionResult> results = detector.detectPhone(null);
        assertTrue(results.isEmpty());
    }

    @Test
    void testDetectPhone_EmptyInput() {
        List<DetectionResult> results = detector.detectPhone("");
        assertTrue(results.isEmpty());
    }

    @Test
    void testDetectIdCard_ValidIdCard() {
        List<DetectionResult> results = detector.detectIdCard("身份证号：110101199001011234");
        assertEquals(1, results.size());
        assertEquals("110101199001011234", results.get(0).getValue());
        assertEquals(SensitiveType.ID_CARD, results.get(0).getSensitiveType());
    }

    @Test
    void testDetectIdCard_InvalidIdCard() {
        List<DetectionResult> results = detector.detectIdCard("身份证号：123456");
        assertEquals(0, results.size());
    }

    @Test
    void testDetectIdCard_NullInput() {
        List<DetectionResult> results = detector.detectIdCard(null);
        assertTrue(results.isEmpty());
    }

    @Test
    void testDetectBankCard_ValidBankCard() {
        List<DetectionResult> results = detector.detectBankCard("卡号：0000000000000000");
        assertEquals(1, results.size());
        assertEquals("0000000000000000", results.get(0).getValue());
        assertEquals(SensitiveType.BANK_CARD, results.get(0).getSensitiveType());
    }

    @Test
    void testDetectBankCard_InvalidBankCard() {
        List<DetectionResult> results = detector.detectBankCard("卡号：1234567890123456");
        assertEquals(0, results.size());
    }

    @Test
    void testDetectBankCard_NullInput() {
        List<DetectionResult> results = detector.detectBankCard(null);
        assertTrue(results.isEmpty());
    }

    @Test
    void testDetectEmail_ValidEmail() {
        List<DetectionResult> results = detector.detectEmail("邮箱：user@example.com");
        assertEquals(1, results.size());
        assertEquals("user@example.com", results.get(0).getValue());
        assertEquals(SensitiveType.EMAIL, results.get(0).getSensitiveType());
    }

    @Test
    void testDetectEmail_MultipleEmails() {
        List<DetectionResult> results = detector.detectEmail("邮箱1：a@test.com，邮箱2：b@test.com");
        assertEquals(2, results.size());
    }

    @Test
    void testDetectEmail_InvalidEmail() {
        List<DetectionResult> results = detector.detectEmail("邮箱：invalid");
        assertEquals(0, results.size());
    }

    @Test
    void testDetectEmail_NullInput() {
        List<DetectionResult> results = detector.detectEmail(null);
        assertTrue(results.isEmpty());
    }

    @Test
    void testDetectAll_MixedContent() {
        String text = "姓名：张三，电话：13812345678，邮箱：zhangsan@example.com，身份证：110101199001011234，卡号：0000000000000000";
        List<DetectionResult> results = detector.detectAll(text);
        assertEquals(4, results.size());
    }
}
