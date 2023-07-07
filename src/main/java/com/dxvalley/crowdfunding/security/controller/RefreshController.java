package com.dxvalley.crowdfunding.security.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dxvalley.crowdfunding.security.jwt.JwtTokenUtil;
import com.dxvalley.crowdfunding.security.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
public class RefreshController {
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/api/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing refresh token");
        }

        String refreshToken = authorizationHeader.substring("Bearer ".length());
        try {
            Algorithm algorithm = Algorithm.HMAC256(JwtTokenUtil.getSecretKey().getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);
            String username = decodedJWT.getSubject();
            UserDetails user = customUserDetailsService.loadUserByUsername(username);

            String accessToken = JwtTokenUtil.generateAccessToken(user, request);
            Map<String, String> tokens = Collections.singletonMap("access_token", accessToken);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream().write(new ObjectMapper().writeValueAsBytes(tokens));
        } catch (JWTVerificationException e) {
            handleTokenVerificationException(response, e);
        }
    }

    private void handleTokenVerificationException(HttpServletResponse response, JWTVerificationException e) throws IOException {
        response.setHeader("error", e.getMessage());
        response.setStatus(HttpStatus.FORBIDDEN.value());

        Map<String, String> error = Collections.singletonMap("error_message", e.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(new ObjectMapper().writeValueAsBytes(error));
    }
}
