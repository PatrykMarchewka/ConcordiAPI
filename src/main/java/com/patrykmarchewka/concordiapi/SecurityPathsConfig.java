package com.patrykmarchewka.concordiapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
public class SecurityPathsConfig {

    @Bean
    public RequestMatcher publicRequestMatcher(){
        return new OrRequestMatcher(
                PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui/**"),
                PathPatternRequestMatcher.withDefaults().matcher("/v3/api-docs/**"),
                PathPatternRequestMatcher.withDefaults().matcher("/login"),
                PathPatternRequestMatcher.withDefaults().matcher("/signup"),
                PathPatternRequestMatcher.withDefaults().matcher("/health")
        );
    }
}
