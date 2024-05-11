package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.util.EmailUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {
    private final Environment env;
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

        try {
            jwt = authHeader.substring(7);
            String authServerUrl = env.getProperty("app.security.auth-server-url");
            WebClient webClient1 = WebClient.builder()
                    .baseUrl(authServerUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            Map authStatus = webClient1.post()
                    .uri("/authenticate")
                    .headers(h -> h.setBearerAuth(jwt))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .exchange()
                    .block()
                    .bodyToMono(Map.class)
                    .block();

            logger.info("==>>  Attempting Authorization for " + jwt + " ... ");

            userEmail = (String) authStatus.get("user");
            String status = (String) authStatus.get("status");

            if (EmailUtil.isEmailValid(userEmail) && status.toUpperCase().equals("AUTHORIZED") ) {
                boolean enabled = true;
                boolean accountNonExpired = true;
                boolean credentialsNonExpired = true;
                boolean accountNonLocked = true;
                Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

                UserDetails userDetails =
                        new User(userEmail, "", enabled, accountNonExpired,
                                credentialsNonExpired, accountNonLocked, authorities);

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

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            filterChain.doFilter(request, response);
        }
    }
}
