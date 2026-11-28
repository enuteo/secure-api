package bes.tcc.secureapi.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
	private static final String NO_AUTHENTICATION = "não há autenticação";
	private static final String FUNCTIONALITY_NOT_AUTHORIZED = "não autorizado a funcionalidade";

	@Override
	public void commence(
			HttpServletRequest request,
			HttpServletResponse response,
			org.springframework.security.core.AuthenticationException exception) throws IOException {
		writeResponse(response, 407, NO_AUTHENTICATION);
	}

	@Override
	public void handle(
			HttpServletRequest request,
			HttpServletResponse response,
			AccessDeniedException exception) throws IOException, ServletException {
		writeResponse(response, HttpServletResponse.SC_UNAUTHORIZED, FUNCTIONALITY_NOT_AUTHORIZED);
	}

	private void writeResponse(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.TEXT_PLAIN_VALUE);
		response.getWriter().write(message);
	}
}
