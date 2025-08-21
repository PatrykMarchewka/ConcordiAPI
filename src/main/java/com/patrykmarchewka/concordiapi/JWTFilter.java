package com.patrykmarchewka.concordiapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.JWTAuthenticationException;
import com.patrykmarchewka.concordiapi.Exceptions.JWTException;
import com.patrykmarchewka.concordiapi.Users.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final SecurityPathsConfig securityPathsConfig;

    @Autowired
    public JWTFilter(UserService userService, SecurityPathsConfig securityPathsConfig) {
        this.userService = userService;
        this.securityPathsConfig = securityPathsConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String authHeader = request.getHeader("Authorization");

        if (!securityPathsConfig.publicRequestMatcher().matches(request)) {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new JWTAuthenticationException("No bearer token provided in authentication header");
            }
            String token = authHeader.substring(7);
            boolean verified = false;
            try {
                verified = JSONWebToken.VerifyJWT(token);
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new JWTException(e.getMessage(), e);
            }
            if (!verified) {
                throw new JWTAuthenticationException("Token has not been verified");
            }
            Map<String, Object> payload;
            try {
                payload = JSONWebToken.ExtractJWTTokenPayload(token);
            } catch (JsonProcessingException e) {
                throw new JWTException(e.getMessage(), e.getCause());
            }
            long exp = Long.valueOf(payload.get("exp").toString());
            long now = System.currentTimeMillis() / 1000;
            if (now > exp) {
                throw new JWTAuthenticationException("Token expired");
            } else {
                Long ID = (Long)payload.get("uID");
                if (ID != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = userService.getUserByID(ID);
                    if (user != null) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, List.of());
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        }


        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }


}
