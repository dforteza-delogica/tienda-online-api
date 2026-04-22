package com.delogica.tienda_api.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler 
{
    // 400 - VALIDACION DE DATOS
@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<Map<String, String>> details = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            Map<String, String> fieldError = new HashMap<>();
            fieldError.put("field", error.getField());
            fieldError.put("message", error.getDefaultMessage());
            details.add(fieldError);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("path", request.getRequestURI());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("code", "VALIDATION_ERROR");
        body.put("message", "Los datos enviados no superan la validación");
        body.put("details", details);

        return (new ResponseEntity<>(body, HttpStatus.BAD_REQUEST));
    }

    // 403 - FORBIDEN
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<Object> handleInvalidOperationException(InvalidOperationException ex, HttpServletRequest request)
    {
        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("path", request.getRequestURI());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("code", "FORBIDDEN");
        body.put("message", ex.getMessage());

        return (new ResponseEntity<>(body, HttpStatus.BAD_REQUEST));
    }
    
    // 404 - RECURSO NO ENCONTRADO
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) 
    {
        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("path", request.getRequestURI());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("code", "RESOURCE_NOT_FOUND");
        body.put("message", ex.getMessage());

        return (new ResponseEntity<>(body, HttpStatus.NOT_FOUND));
    }

    // 409 - CONFLICTO
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(ConflictException ex, HttpServletRequest request) 
    {
        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("path", request.getRequestURI());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("code", "CONFLICT");
        body.put("message", ex.getMessage());

        return (new ResponseEntity<>(body, HttpStatus.CONFLICT));
    }

    // 409 - ERROR DE INTEGRIDAD DE DATOS
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) 
    {
        Map<String, Object> body = new HashMap<>();
        
        body.put("timestamp", LocalDateTime.now());
        body.put("path", request.getRequestURI());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("code", "CONFLICT");
        body.put("message", "Error de integridad de datos");
        body.put("details", ex.getMostSpecificCause().getMessage());

        return (new ResponseEntity<>(body, HttpStatus.CONFLICT));
    }

    // 500 - ERROR INTERNO DEL SERVIDOR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, HttpServletRequest request)
    {
        Map<String, Object> body = new HashMap<>();
        
        body.put("timestamp", LocalDateTime.now());
        body.put("path", request.getRequestURI());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("code", "INTERNAL_ERROR");
        body.put("message", "Error interno del servidor");

        return (new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR));
    }

}
