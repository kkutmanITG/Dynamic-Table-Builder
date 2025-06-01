package kg.service.dynamictablebuilder.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dynamic Table Builder")
                        .description("Динамический Конструктор Таблиц и Универсальный CRUD API для PostgreSQL"));
    }
}