package com.intuit.test.rest.spring;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an important configuration which setups and provides Api documentation bean
 */
@Configuration
public class OpenApiExposure {
    static class Entries {
        @Value ("{@api.docs.title:player API}")
        String apiTitle;

        @Value ("{@api.docs.description:brief}")
        String apiDescription;

        @Value ("{@api.docs.version:0.1}")
        String apiVersion;
    }

    @Bean
    static Entries apiDocEntries(){

        return new Entries();

    }

    /**
     * provides automatic document  generation object
     * @param properties
     * @return
     */

    @Bean
    public OpenAPI plaeyMicroserviceOpenAPI(Entries properties) {

        return new OpenAPI()
                .info(new Info().title(properties.apiTitle)
                        .description(properties.apiDescription)
                        .version(properties.apiVersion));

    }

}
