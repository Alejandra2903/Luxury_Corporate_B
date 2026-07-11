package com.example.luxury.dominios.common.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException ex) {
		return error(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Map<String, Object>> business(BusinessException ex) {
		return error(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, Object>> accessDenied(AccessDeniedException ex) {
		return error(HttpStatus.FORBIDDEN, "Acceso denegado");
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
		Map<String, String> fields = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
		Map<String, Object> body = base(HttpStatus.BAD_REQUEST, "Validacion fallida");
		body.put("fields", fields);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> unexpected(Exception ex) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
	}

	private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(base(status, message));
	}

	private Map<String, Object> base(HttpStatus status, String message) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", Instant.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		return body;
	}
}
