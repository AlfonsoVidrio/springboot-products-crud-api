package com.alfonsovidrio.springboot.app.springboot_crud.security.filter;

import static com.alfonsovidrio.springboot.app.springboot_crud.security.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.alfonsovidrio.springboot.app.springboot_crud.security.SimpleGrantedAuthorityJsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Arrays;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        // If there's no token or doesn't have the correct format, continue without authentication
        if (!isValidAuthorizationHeader(authorizationHeader)) {
            chain.doFilter(request, response);
            return;
        }

        String token = extractTokenFromHeader(authorizationHeader);

        try {
            authenticateUser(token);
            chain.doFilter(request, response);
        } catch (JwtException e) {
            handleJwtException(response, e);
        } catch (Exception e) {
            handleGenericException(response, e);
        }
    }

    /**
     * Verifies if the authorization header has the correct format
     */
    private boolean isValidAuthorizationHeader(String header) {
        return header != null && header.startsWith(PREFIX_TOKEN);
    }

    /**
     * Extracts the token from the authorization header
     */
    private String extractTokenFromHeader(String header) {
        return header.replace(PREFIX_TOKEN, "");
    }

    /**
     * Authenticates the user based on the JWT token
     */
    private void authenticateUser(String token) {
        Claims claims = parseJwtToken(token);
        String username = claims.getSubject();
        Collection<? extends GrantedAuthority> authorities = extractAuthorities(claims);

        UsernamePasswordAuthenticationToken authenticationToken = 
            new UsernamePasswordAuthenticationToken(username, null, authorities);
        
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    /**
     * Parses and validates the JWT token
     */
    private Claims parseJwtToken(String token) {
        return Jwts.parser()
            .verifyWith(SECRET_KEY)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Extracts the authorities from the JWT token
     */
    private Collection<? extends GrantedAuthority> extractAuthorities(Claims claims) {
        Object authoritiesClaims = claims.get("authorities");
        
        try {
            return Arrays.asList(new ObjectMapper()
                .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));
        } catch (Exception e) {
            throw new JwtException("Error processing authorities from JWT token", e);
        }
    }

    /**
     * Handles JWT specific exceptions
     */
    private void handleJwtException(HttpServletResponse response, JwtException e) throws IOException {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Invalid JWT token");
        body.put("message", e.getMessage());
        
        sendErrorResponse(response, body, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles generic exceptions
     */
    private void handleGenericException(HttpServletResponse response, Exception e) throws IOException {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Authentication error");
        body.put("message", e.getMessage());
        
        sendErrorResponse(response, body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Sends an error response in JSON format
     */
    private void sendErrorResponse(HttpServletResponse response, Map<String, String> body, HttpStatus status) 
            throws IOException {
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(status.value());
        response.setContentType(CONTENT_TYPE);
    }
}
