package br.com.everrise.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI everriseOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("EVERRISE Medical Solutions API")
                        .version("v1")
                        .description("API para controle de guincho hospitalar autonomo com telemetria em tempo real, suporte a IoT via MQTT, SSE Streaming e integracao com Mercado Pago")
                        .contact(new Contact()
                                .name("EverRise Support")
                                .email("support@everrise.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}git status
git add .
git commit -m "chore: additional fixes for backend/docker setup"