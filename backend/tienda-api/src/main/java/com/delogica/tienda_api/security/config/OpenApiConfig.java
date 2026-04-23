package com.delogica.tienda_api.security.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {


    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Tienda Online API")
                        .version("1.0")
                        .description("API RESTful para la gestión de ventas de una tienda online")
                        .contact(new Contact()
                                .name("Diego Forteza")
                                .email("diego.forteza@delogica.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
}