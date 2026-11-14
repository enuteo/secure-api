package bes.tcc.secureapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import bes.tcc.secureapi.dto.MyObject;
import bes.tcc.secureapi.repository.BaseObjectRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureMockMvc
class ObjectAuthorizationTests {
	private static final Map<String, Map<String, Object>> TOKENS = Map.of(
			"object-role", Map.of("realm_access", Map.of("roles", List.of("objectrole"))),
			"other-role", Map.of("realm_access", Map.of("roles", List.of("user"))),
			"client-object-role", Map.of(
					"resource_access", Map.of("auth-api-test", Map.of("roles", List.of("objectrole")))),
			"other-client-role", Map.of(
					"resource_access", Map.of("another-client", Map.of("roles", List.of("objectrole")))));

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BaseObjectRepository baseObjectRepository;

	@BeforeEach
	void setUpRepository() {
		when(baseObjectRepository.findById(42L)).thenReturn(Optional.of(new MyObject(42L, "Test object")));
	}

	@Test
	void loadsAuthorizedObjectsFromRepository() throws Exception {
		mockMvc.perform(get("/object/42").header(HttpHeaders.AUTHORIZATION, "Bearer object-role"))
				.andExpect(status().isOk());
	}

	@Test
	void requiresAuthentication() throws Exception {
		mockMvc.perform(get("/object/42"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void rejectsAuthenticatedUserWithoutObjectRole() throws Exception {
		mockMvc.perform(get("/object/42").header(HttpHeaders.AUTHORIZATION, "Bearer other-role"))
				.andExpect(status().isForbidden());
	}

	@Test
	void allowsRealmObjectRole() throws Exception {
		mockMvc.perform(get("/object/42").header(HttpHeaders.AUTHORIZATION, "Bearer object-role"))
				.andExpect(status().isOk());
	}

	@Test
	void allowsObjectRoleFromConfiguredClient() throws Exception {
		mockMvc.perform(get("/object/42").header(HttpHeaders.AUTHORIZATION, "Bearer client-object-role"))
				.andExpect(status().isOk());
	}

	@Test
	void ignoresObjectRoleFromAnotherClient() throws Exception {
		mockMvc.perform(get("/object/42").header(HttpHeaders.AUTHORIZATION, "Bearer other-client-role"))
				.andExpect(status().isForbidden());
	}

	@Test
	void deniesUnconfiguredEndpoints() throws Exception {
		mockMvc.perform(get("/unconfigured").header(HttpHeaders.AUTHORIZATION, "Bearer object-role"))
				.andExpect(status().isForbidden());
	}

	@TestConfiguration
	static class TestJwtDecoderConfig {
		@Bean
		JwtDecoder jwtDecoder() {
			return token -> {
				Map<String, Object> claims = TOKENS.get(token);
				if (claims == null) {
					throw new BadJwtException("Unknown test token");
				}
				return Jwt.withTokenValue(token)
						.header("alg", "RS256")
						.subject(token)
						.claim("preferred_username", token)
						.claims(existing -> existing.putAll(claims))
						.issuedAt(Instant.now())
						.expiresAt(Instant.now().plusSeconds(300))
						.build();
			};
		}
	}
}
