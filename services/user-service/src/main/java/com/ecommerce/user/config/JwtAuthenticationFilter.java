
package com.ecommerce.user.config;

import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.service.CustomUserDetailsService;
import com.ecommerce.user.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            UserRepository userRepository) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // No Bearer token: let Spring Security handle the request.
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7).trim();

        if (token.isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing JWT token");
            return;
        }

        try {
            String email = jwtService.extractUsername(token)
                    .trim()
                    .toLowerCase(Locale.ROOT);

            if (SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                // Load the latest account state from the database.
                User user = userRepository.findByEmail(email)
                        .orElse(null);

                if (user == null) {
                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "User not found");
                    return;
                }

                // Reject tokens belonging to disabled accounts.
                if (!user.isEnabled()) {
                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "Account is disabled");
                    return;
                }

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                // Verify signature, expiry, username and token version.
                if (!jwtService.isTokenValid(
                        token, userDetails, user.getTokenVersion())) {

                    logger.warn("JWT rejected: token version mismatch or token invalid");
                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "Invalid, expired, or revoked JWT");
                    return;
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (JwtException | IllegalArgumentException ex) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired JWT");
            return;

        } catch (UsernameNotFoundException ex) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "User not found");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
