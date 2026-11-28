package bes.tcc.secureapi.controller;

import bes.tcc.secureapi.dto.MyObject;
import bes.tcc.secureapi.repository.BaseObjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Objects", description = "Operations protected by Keycloak roles")
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT")
public class ObjectController {
	private final BaseObjectRepository baseObjectRepository;

	public ObjectController(BaseObjectRepository baseObjectRepository) {
		this.baseObjectRepository = baseObjectRepository;
	}

	@GetMapping("/object/{id}")
	@Operation(
			summary = "Get an object by ID",
			description = "Requires the objectrole role and ownership of the object or a position above its owner in the user hierarchy.",
			security = @SecurityRequirement(name = "bearerAuth"),
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Object returned successfully.",
							content = @Content(schema = @Schema(implementation = MyObject.class))),
					@ApiResponse(responseCode = "401", description = "Não autorizado a funcionalidade."),
					@ApiResponse(responseCode = "403", description = "Não tem permissão de acessar o recurso."),
					@ApiResponse(responseCode = "407", description = "Não há autenticação."),
					@ApiResponse(responseCode = "404", description = "No object exists with the requested ID.")
			})
	public ResponseEntity<MyObject> getObjectById(@PathVariable Long id, Authentication authentication) {
		Optional<MyObject> object = baseObjectRepository.findByIdVisibleToUser(id, authentication.getName());
		return ResponseEntity.of(object);
	}
}
