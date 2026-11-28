package bes.tcc.secureapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AccessNotAllowedException.class)
	public ResponseEntity<ProblemDetail> handleAccessNotAllowed(AccessNotAllowedException exception) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
	}
}
