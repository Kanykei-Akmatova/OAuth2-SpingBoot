package org.ac.cst8277.akmatova.kanykei.usermanagementservice.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.ac.cst8277.akmatova.kanykei.usermanagementservice.services.JwtTokenService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenService jwtService;
  private final UserDetailsService userDetailsService;

  private static final Logger logger = LogManager.getLogger(JwtTokenAuthenticationFilter.class);

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    final String authHeader = request.getHeader(AuthConfigurationConstants.HEADER_AUTHORIZATION);
    final String jwt;
    final String userEmail;

    if (authHeader == null || !authHeader.startsWith(AuthConfigurationConstants.BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    jwt = authHeader.substring(7);

    try {
      String uri = request.getRequestURI();
      logger.info("==>>  Attempting Authorization for " + uri + " ... ");

      userEmail = jwtService.extractUsername(jwt);

      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

        if (jwtService.isTokenValid(jwt, userDetails)) {

          Authentication auth = SecurityContextHolder.getContext().getAuthentication();
          if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
          }
          SecurityContextHolder.getContext().setAuthentication(null);

          UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);

          logger.info("===>>> (Authorized) Authentication: " + authentication);
        }
      }

      filterChain.doFilter(request, response);
    } catch (Exception exception) {
      filterChain.doFilter(request, response);
    }
  }
}
