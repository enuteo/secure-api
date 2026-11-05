package bes.tcc.secureapi.security;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class KeycloakRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {
	private final String clientId;

	public KeycloakRoleConverter(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		Set<GrantedAuthority> authorities = Stream.concat(realmRoles(jwt), clientRoles(jwt))
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toSet());
		String username = jwt.hasClaim("preferred_username")
				? jwt.getClaimAsString("preferred_username")
				: jwt.getSubject();
		return new JwtAuthenticationToken(jwt, authorities, username);
	}

	private Stream<String> realmRoles(Jwt jwt) {
		return roles(jwt.getClaimAsMap("realm_access"));
	}

	private Stream<String> clientRoles(Jwt jwt) {
		Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
		if (clientId == null || resourceAccess == null
				|| !(resourceAccess.get(clientId) instanceof Map<?, ?> clientAccess)) {
			return Stream.empty();
		}
		return roles(clientAccess);
	}

	private Stream<String> roles(Map<?, ?> access) {
		if (access == null || !(access.get("roles") instanceof Collection<?> roles)) {
			return Stream.empty();
		}
		return roles.stream().map(String::valueOf);
	}
}
