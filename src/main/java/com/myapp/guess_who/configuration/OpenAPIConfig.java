package com.myapp.guess_who.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Development");

        Contact contactPerson = new Contact();
        contactPerson.setName("Daniel Mazurczak");
        contactPerson.setEmail("daniel.t.mazurczak@gmail.com");

        Info information = new Info()
            .title("Guess Who API")
            .version("1.0")
            .description("This API exposes endpoints for Guess Who game.")
            .contact(contactPerson);

        return new OpenAPI().info(information).servers(List.of(server));
    }
}
