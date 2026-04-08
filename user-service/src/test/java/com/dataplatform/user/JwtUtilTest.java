package com.dataplatform.user;

import com.dataplatform.user.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=dGhpcyBpcyBhIHZlcnkgbG9uZyBzZWNyZXQga2V5IGZvciBqd3QgdG9rZW4gZ2VuZXJhdGlvbiBhbmQgdmFsaWRhdGlvbg==",
    "jwt.expiration=3600",
    "jwt.refreshExpiration=604800"
})
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USERNAME = "testuser";

    @Test
    void generateToken_Success() {
        String token = jwtUtil.generateToken(TEST_USER_ID, TEST_USERNAME);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void validateToken_ValidToken_ReturnsClaims() {
        String token = jwtUtil.generateToken(TEST_USER_ID, TEST_USERNAME);

        Claims claims = jwtUtil.validateToken(token);

        assertNotNull(claims);
        assertEquals(TEST_USER_ID.toString(), claims.getSubject());
    }

    @Test
    void validateToken_InvalidToken_ReturnsNull() {
        Claims claims = jwtUtil.validateToken("invalid.token.here");

        assertNull(claims);
    }

    @Test
    void extractUserId_ValidToken_ReturnsUserId() {
        String token = jwtUtil.generateToken(TEST_USER_ID, TEST_USERNAME);

        Long userId = jwtUtil.extractUserId(token);

        assertEquals(TEST_USER_ID, userId);
    }

    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        String token = jwtUtil.generateToken(TEST_USER_ID, TEST_USERNAME);

        String username = jwtUtil.extractUsername(token);

        assertEquals(TEST_USERNAME, username);
    }

    @Test
    void generateRefreshToken_Success() {
        String refreshToken = jwtUtil.generateRefreshToken(TEST_USER_ID);

        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());

        Claims claims = jwtUtil.validateToken(refreshToken);
        assertNotNull(claims);
        assertEquals("refresh", claims.get("type"));
    }

    @Test
    void isTokenExpired_ValidToken_ReturnsFalse() {
        String token = jwtUtil.generateToken(TEST_USER_ID, TEST_USERNAME);

        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void isTokenExpired_InvalidToken_ReturnsTrue() {
        assertTrue(jwtUtil.isTokenExpired("invalid.token"));
    }

    @Test
    void getExpiration_ReturnsConfiguredValue() {
        assertEquals(3600L, jwtUtil.getExpiration());
    }
}
