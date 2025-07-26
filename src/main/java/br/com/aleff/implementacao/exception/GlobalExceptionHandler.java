package br.com.aleff.implementacao.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validação dos campos (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "Falha na Validação", errors, request.getRequestURI());
    }

    // Violação de unicidade, chaves duplicadas
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        String field = extractDuplicateField(ex.getMessage());
        String message = (field != null)
                ? "O campo '" + field + "' já está em uso."
                : "Violação de integridade de dados.";

        return buildResponse(
                HttpStatus.CONFLICT,
                message,
                null,
                request.getRequestURI()
        );
}

    // Erro de autenticação (geral)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Não autenticado. Faça login.", null, request.getRequestURI());
    }

    // Erro de acesso negado (permissões)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Usuário não autorizado", null, request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null, request.getRequestURI());
    }

    // Erro genérico para qualquer exceção não prevista
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        ex.printStackTrace(); // loga stack trace para debugging
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor", null, request.getRequestURI());
    }

    // Método auxiliar para montar o corpo da resposta JSON padronizada
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message, Object errors, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if(errors != null) body.put("errors", errors);
        body.put("path", path);
        return new ResponseEntity<>(body, status);
    }

    private String extractDuplicateField(String errorMessage) {
    if (errorMessage == null) return null;

    if (errorMessage.contains("Key (")) {
        int start = errorMessage.indexOf("Key (") + 5;
        int end = errorMessage.indexOf(")=", start);
        if (end > start) {
            return errorMessage.substring(start, end);
        }
    }
    return null;
}
}
