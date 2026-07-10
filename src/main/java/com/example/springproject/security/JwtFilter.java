package com.example.springproject.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    // This filter runs on every request and, once the JWT is validated,
    // stores the authenticated user in the SecurityContextHolder.
    // Controllers can then simply inject `Authentication` to get the current user.

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 0. Allow whitelisted paths through without authentication
        String path = request.getRequestURI();
        if (path.equals("/login") || path.equals("/register") || path.equals("/hello")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Extract the token from the Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. Check the token is in the expected "Bearer <token>" format
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7); // strip "Bearer " prefix

            // 3. Validate the token
            if (jwtUtil.validateToken(token)) {

                // 4. Extract the username from the token
                String username = jwtUtil.getUsername(token);

                // 5. Tell Spring Security who this request belongs to
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,                    // principal (username, password, role)
                                null,                           // credentials (already verified, not needed)
                                userDetails.getAuthorities()    // granted authorities (e.g. ROLE_USER)
                        );
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            } else {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("Token invalid or expired");
                return;
            }
        }

        // 6. Continue down the filter chain
        filterChain.doFilter(request, response);
    }

    // Login flow reference:
    // 1. User submits username + password
    // 2. AuthenticationManager verifies credentials against the database
    // 3. Role is retrieved as part of authentication
    // 4. A JWT is generated and returned to the client
}