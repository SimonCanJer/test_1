package com.intuit.test.spring.security;


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
 * Configs security temporary while is not defined a concrete rules
 */
@Configuration
///@EnableWebSecurity
public class SecuriryConfig {
    /** common api uri mapping property */
    public static final String API_URI = "api.uri";

    /** player uri mapping property */
    public static final String API_PLAYERS_URI = "api.players.uri";

    public static final String URI_SECURITY_OPEN="security.uris.open";

    public static final String SECURITY_OPEN="security.protect.api";
    public static final String PLAYERS_URI = "/players";

    static class SecurityProperties {

        @Value("${security.uris.open:not}")
        String sharedUri;

        @Value("${security.protect.api:true}")
        boolean protectApi;
    }

    @Bean
    SecurityProperties securityPorperties(){
        return new SecurityProperties();
    }

    /**
     * creates, configures and exports security filter chain bean {@link SecurityFilterChain}
     * @param http - security bean object
     * @param env  - {@link Environment} instance
     * @return       the bean announces
     * @throws Exception - and exception can be thrown while configuring
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, Environment env, @Autowired SecurityProperties properties) throws Exception {

        String api = env.getProperty(API_URI, "/api");
        List<String> sharedApi= new ArrayList<>();
        if(!properties.protectApi) {
            String players = env.getProperty(API_PLAYERS_URI, PLAYERS_URI);
            String playersFull = String.format("%s%s", api, players);
            sharedApi.add(String.format("%s/*", playersFull));
            sharedApi.add(playersFull);
        }
        String[] additionalShared=properties.sharedUri.split("\\,");
        for(String s:additionalShared)
            sharedApi.add(s);
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests((c) -> {
            c.requestMatchers(sharedApi.toArray(new String[1])).permitAll().anyRequest().authenticated();
        });
        return http.build();

    }


}
