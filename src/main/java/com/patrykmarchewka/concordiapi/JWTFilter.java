package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Users.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;
import java.util.Map;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain){
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            try {
                if (JSONWebToken.VerifyJWT(token)) {
                    Map<String,Object> payload = JSONWebToken.ExtractJWTTokenPayload(token);
                    long exp = Long.valueOf(payload.get("exp").toString());
                    long now = System.currentTimeMillis()/1000;
                    if (now > exp){
                        throw new RuntimeException("Token expired");
                    }
                    else {
                        String login = (String)payload.get("login");
                        if (login != null && SecurityContextHolder.getContext().getAuthentication() == null){
                            User user = userService.getUserByLogin(login);
                            String password = (String) payload.get("password");
                            if (user != null && (Passwords.CheckPasswordBCrypt(password,user.getPassword()) || user.getPassword().equals(password))){
                                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user,null, List.of());
                                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                            }
                        }
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            filterChain.doFilter(request,response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
