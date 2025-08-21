package com.patrykmarchewka.concordiapi;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTFilter jwtFilter;
    private final SecurityPathsConfig securityPathsConfig;

    @Autowired
    public SecurityConfig(JWTFilter jwtFilter, SecurityPathsConfig securityPathsConfig){
        this.jwtFilter = jwtFilter;
        this.securityPathsConfig = securityPathsConfig;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable()) //We use JWT not cookies
                .formLogin(form -> form.disable()) //Disables default login, we use JWT
                .httpBasic(http -> http.disable()) //Disables basic auth, we use JWT
                .authorizeHttpRequests(auth -> auth.requestMatchers(securityPathsConfig.publicRequestMatcher()).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"" + "You are not authenticated" + "\"}");
                        })
                )
                .build();
    }
}
