package org.ac.cst8277.akmatova.kanykei.usermanagementservice.configuration;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;


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
                .requestMatchers( "/").permitAll()
                .requestMatchers("/access/token").permitAll()
                .requestMatchers("/authenticate").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(jwtTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Enable oauth2 login, and forward successful logins to the `/access/token` route
                .oauth2Login()
                .defaultSuccessUrl("/access/token", true)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                )
                .and()
                .logout(logout -> logout
                        .logoutUrl("/basic/basiclogout")
                        .addLogoutHandler(new SecurityContextLogoutHandler())
                );

        return httpSecurity.build();
    }
}
