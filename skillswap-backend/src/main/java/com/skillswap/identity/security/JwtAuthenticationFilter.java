package com.skillswap.identity.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsServiceImpl userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        String requestUri = request.getRequestURI();

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            System.out.println(
                    "[JWT] No Bearer token | " + requestUri
            );

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorizationHeader.substring(7).trim();

        if (token.isBlank()) {

            System.out.println(
                    "[JWT] Bearer token is blank | " + requestUri
            );

            filterChain.doFilter(request, response);
            return;
        }

        try {

            if (!jwtService.isAccessToken(token)) {

                System.out.println(
                        "[JWT] Token rejected: not an ACCESS token | "
                                + requestUri
                );

                filterChain.doFilter(request, response);
                return;
            }

            String username =
                    jwtService.tryExtractUsername(token);

            if (username == null ||
                    username.isBlank()) {

                System.out.println(
                        "[JWT] Token rejected: username could not be extracted | "
                                + requestUri
                );

                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() != null) {

                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(username);

            if (!userDetails.isEnabled()) {

                System.out.println(
                        "[JWT] Token rejected: user is disabled | "
                                + requestUri
                );

                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtService.isAccessTokenValid(
                    token,
                    userDetails
            )) {

                System.out.println(
                        "[JWT] Token rejected: access-token validation failed | "
                                + requestUri
                );

                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken
                    authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            System.out.println(
                    "[JWT] Authentication successful | "
                            + requestUri
                            + " | user="
                            + username
            );

        } catch (Exception exception) {

            System.out.println(
                    "[JWT] Authentication exception | "
                            + requestUri
                            + " | "
                            + exception.getClass().getSimpleName()
                            + " | "
                            + exception.getMessage()
            );

            SecurityContextHolder
                    .clearContext();
        }

        filterChain.doFilter(request, response);
    }
}