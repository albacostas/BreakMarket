package com.albadiego.breakmarket.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("API de Gestión del Sistema de Apuestas BreakMarket")
                    .version("1.0.0")
                    .description("API REST para la gestión de apuestas abiertas, apuestas realizadas, pruebas para la resolución de apuestas y usuarios")
                        // Añadir licencia
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo Local")
                ));
    }
}
