package com.liverpool.products.conf;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConf {
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(
                        new io.swagger.v3.oas.models.info.Info()
                                .title("Liverpool | Products Catalogue")
                                .summary("Products API")
                                .description("Show full flow for manage products.")
                                .version("v1")
                                .termsOfService("This collection is only for exclusive use for testing phase.")
                                .contact(
                                        new Contact()
                                                .name("Iván H. Botello Fermoso")
                                                .email("eddirockvan86@gmail.com")
                                )
                                .license(
                                        new io.swagger.v3.oas.models.info.License()
                                                .name("Apache 2.0")
                                                .url("http://springdoc.org")
                                )
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("SpringShop Wiki Documentation")
                                .url("https://springshop.wiki.github.org/docs")
                );
    }
}
