package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.configuration;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Defines the spring security configuration for our application via the `getSecurityFilterChain`
 * method
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    /**
     * @param httpSecurity is injected by spring security
     */
    @Bean
    SecurityFilterChain getSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // Enable CORS and disable CSRF
        httpSecurity = httpSecurity.cors().and().csrf().disable();

        // Authorize all requests coming through, and ensure that they are authenticated
        httpSecurity
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(jwtTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                );

        return httpSecurity.build();
    }
}
