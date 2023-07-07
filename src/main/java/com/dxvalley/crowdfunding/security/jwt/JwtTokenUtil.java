package com.dxvalley.crowdfunding.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dxvalley.crowdfunding.security.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class JwtTokenUtil {
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000; // 30 minutes
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 24 * 7 * 60 * 60 * 1000; // one week
    public static String SECRET_KEY;
    private static CustomUserDetailsService customUserDetailsService;

    public JwtTokenUtil(@Value("${SECRET_KEY}") String privateKey, CustomUserDetailsService customUserDetailsService) {
        JwtTokenUtil.SECRET_KEY = privateKey;
        JwtTokenUtil.customUserDetailsService = customUserDetailsService;
    }

    public static String generateAccessToken(UserDetails user, HttpServletRequest request) {
        customUserDetailsService.updateLastLogin(user.getUsername());

        List<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME)) // 30 minutes
                .withClaim("authorities", authorities)
                .sign(Algorithm.HMAC256(SECRET_KEY.getBytes()));
    }

    public static String generateRefreshToken(UserDetails user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .withIssuer(request.getRequestURL().toString())
                .sign(Algorithm.HMAC256(SECRET_KEY.getBytes()));
    }

    public static String getSecretKey() {
        return SECRET_KEY;
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}

