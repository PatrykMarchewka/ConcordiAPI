package com.example.javaspringbootapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
    httpSecurity.csrf().disable()
            .formLogin().disable() //Disables default login
            .httpBasic().disable() //Disables basic auth
            .authorizeHttpRequests()
            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers("/v3/api-docs/**").permitAll()
            .requestMatchers("/login").permitAll()
            .requestMatchers("/signup").permitAll()
            .requestMatchers("/teams/**","/tasks/**","/subtasks/**","/invitations/**","/users/**").denyAll()
            .anyRequest().authenticated()
            .and().addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
    }
}
