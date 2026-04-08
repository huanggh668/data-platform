#!/bin/bash
# Integration tests for all services
set -e

API_HOST="192.168.70.197"
USER_PORT=8080
DESENSITIZE_PORT=8081
WATERMARK_PORT=8082
ENCRYPT_PORT=8083

echo "=== Integration Tests ==="

echo ""
echo "1. Testing User Registration..."
REGISTER_RESP=$(curl -s -X POST "http://$API_HOST:$USER_PORT/api/v1/users/register" \
    -H "Content-Type: application/json" \
    -d '{"username":"testuser","password":"Test@123","email":"test@example.com","phone":"13812345678"}')
echo "Response: $REGISTER_RESP"

echo ""
echo "2. Testing User Login..."
LOGIN_RESP=$(curl -s -X POST "http://$API_HOST:$USER_PORT/api/v1/users/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"testuser","password":"Test@123"}')
echo "Response: $LOGIN_RESP"

TOKEN=$(echo $LOGIN_RESP | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Token: ${TOKEN:0:50}..."

echo ""
echo "3. Testing Desensitization (Phone)..."
MASK_RESP=$(curl -s -X POST "http://$API_HOST:$DESENSITIZE_PORT/api/v1/desensitize" \
    -H "Content-Type: application/json" \
    -d '{"value":"13812345678","type":"PHONE"}')
echo "Response: $MASK_RESP"

echo ""
echo "4. Testing Desensitization (ID Card)..."
MASK_RESP=$(curl -s -X POST "http://$API_HOST:$DESENSITIZE_PORT/api/v1/desensitize" \
    -H "Content-Type: application/json" \
    -d '{"value":"110101199001011234","type":"ID_CARD"}')
echo "Response: $MASK_RESP"

echo ""
echo "5. Testing Auto Desensitize..."
AUTO_RESP=$(curl -s -X POST "http://$API_HOST:$DESENSITIZE_PORT/api/v1/desensitize/auto" \
    -H "Content-Type: application/json" \
    -d '{"phone":"13812345678","email":"test@example.com"}')
echo "Response: $AUTO_RESP"

echo ""
echo "6. Testing Text Watermark Embed..."
WATERMARK_RESP=$(curl -s -X POST "http://$API_HOST:$WATERMARK_PORT/api/v1/watermark/embed/text" \
    -H "Content-Type: application/json" \
    -d '{"text":"Sensitive Data","userId":1,"dataType":"test"}')
echo "Response: $WATERMARK_RESP"

echo ""
echo "7. Testing Watermark Verify..."
VERIFY_RESP=$(curl -s -X POST "http://$API_HOST:$WATERMARK_PORT/api/v1/watermark/verify" \
    -H "Content-Type: application/json" \
    -d '{"data":"'"$WATERMARK_RESP"'"}')
echo "Response: $VERIFY_RESP"

echo ""
echo "8. Testing AES Encryption..."
ENCRYPT_RESP=$(curl -s -X POST "http://$API_HOST:$ENCRYPT_PORT/api/v1/encrypt" \
    -H "Content-Type: application/json" \
    -d '{"plaintext":"Hello World","algorithm":"AES","key":"your-256-bit-key-here-32ch"}')
echo "Response: $ENCRYPT_RESP"

echo ""
echo "=== Integration Tests Complete ==="
