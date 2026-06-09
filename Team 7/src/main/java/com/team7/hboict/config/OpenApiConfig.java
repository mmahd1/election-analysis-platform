package com.team7.hboict.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Verkiezingsuitslagen API",
                version = "1.0",
                description = "API voor verkiezingsuitslagen, waaronder kandidaten en hun stemmen."
        )
)
public class OpenApiConfig {
}
