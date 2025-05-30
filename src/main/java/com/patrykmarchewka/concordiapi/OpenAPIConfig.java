package com.patrykmarchewka.concordiapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI custom(){
        return new OpenAPI().info(new Info().title("ConcordiAPI").version("v1.0.0").description("REST API for managing tasks,users and teams")).components(new Components()
                        .addResponses("200", new ApiResponse().description("Success"))
                        .addResponses("201", new ApiResponse().description("Item created"))
                        .addResponses("400", new ApiResponse().description("Bad request"))
                        .addResponses("401", new ApiResponse().description("You are not authenticated"))
                        .addResponses("403",new ApiResponse().description("You are not authorized to do that action"))
                        .addResponses("404", new ApiResponse().description("Resource was not found"))
                        .addResponses("409", new ApiResponse().description("Conflict occurred"))
                        .addResponses("500",new ApiResponse().description("Unexpected error")));
    }
}
