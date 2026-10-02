package com.osulaa.possystem.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.osulaa.possystem.payload.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(
                                                ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ApiResponse<Object>> handleDuplicate(
                        DuplicateResourceException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(
                                                ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiResponse<Object>> handleInvalid(InvalidCredentialsException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(
                                                ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                                .getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return ResponseEntity.badRequest()
                                .body(
                                                ApiResponse.error("Error de validación", errors));
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleBadRequest(
                        BadRequestException ex) {
                Map<String, String> errors = new HashMap<>();
                errors.put("error", ex.getMessage());

                return ResponseEntity.badRequest()
                                .body(
                                                ApiResponse.error("Error de validación", errors));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
                ex.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(
                                                ApiResponse.error("Ha ocurrido un error interno."));
        }

}
