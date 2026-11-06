package bes.tcc.secureapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI secureApiOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Secure API")
						.version("1.0.0")
						.description("Object API secured with Keycloak bearer tokens."));
	}
}
