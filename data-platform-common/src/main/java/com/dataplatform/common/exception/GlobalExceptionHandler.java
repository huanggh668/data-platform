package com.dataplatform.common.exception;

import com.dataplatform.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

/**
 * 全局统一异常处理器
 * <p>
 * 安全原则：任何未处理异常的内部堆栈信息不暴露给调用方，
 * 仅返回通用错误描述；已知业务异常允许传递 message。
 *
 * @author system
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常：由 service 层主动抛出，message 可以对外展示 */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /** 参数校验失败（@Valid / @Validated） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return ApiResponse.badRequest(message);
    }

    /** 表单绑定失败 */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("绑定异常: {}", message);
        return ApiResponse.badRequest(message);
    }

    /** Bean Validation 约束违反 */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束校验失败: {}", message);
        return ApiResponse.badRequest(message);
    }

    /** 非法参数 */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        return ApiResponse.badRequest(e.getMessage());
    }

    /**
     * Token/权限不足异常（Controller 层主动抛出 RuntimeException 时进入此分支）
     * <p>
     * 对于 "Invalid token" / "Access denied" 等已知权限提示，直接对外返回；
     * 其他 RuntimeException 则屏蔽内部细节，统一返回 401 或 500。
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage();
        if (msg != null && (msg.contains("Invalid token")
                || msg.contains("Missing or invalid Authorization")
                || msg.contains("Access denied"))) {
            log.warn("鉴权异常: {}", msg);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.unauthorized(msg));
        }
        if (msg != null && (msg.contains("already exists")
                || msg.contains("not found")
                || msg.contains("incorrect")
                || msg.contains("disabled")
                || msg.contains("Invalid username or password"))) {
            log.warn("业务运行时异常: {}", msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, msg));
        }
        // 其他未预期的运行时异常，不暴露内部细节
        log.error("运行时异常（未预期）: {}", msg, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("操作失败，请稍后重试"));
    }

    /** 兜底：所有其他异常，隐藏内部实现细节 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("未捕获异常: {}", e.getMessage(), e);
        return ApiResponse.error("服务器内部错误，请联系管理员");
    }
}

