package org.ac.cst8277.akmatova.kanykei.usermanagementservice.configuration;

import lombok.RequiredArgsConstructor;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dao.UserDao;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

  private static String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.github.";
  private final Environment env;
  private final UserDao userDao;

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      var user = userDao.findByEmail(username);
      HashMap<UUID, List<Role>> usersRoles = userDao.findUsersRoles();
      user.setRoles(usersRoles.get(user.getId()));
      
      if (user != null) {
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
      }
      throw new UsernameNotFoundException("User not found");
    };
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository() {
    String clientId = env.getProperty(CLIENT_PROPERTY_KEY + "clientId");
    String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + "clientSecret");

    // Create a new client registration
    return new InMemoryClientRegistrationRepository(
            // spring security provides us pre-populated builders for popular
            // oauth2 providers, like Github, Google, or Facebook.
            // The `CommonOAuth2Provider` contains the definitions for these
            CommonOAuth2Provider.GITHUB.getBuilder("github")
                    // In this case, most of the common configuration, like the token and user endpoints
                    // are pre-populated. We only need to supply our client ID and secret
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .scope("read:user")
                    .scope("user:email")
                    // ^ these credentials are fake, so you'll have to use your own :)
                    .build());
  }

  @Bean
  public WebClient webClient(ClientRegistrationRepository clients, OAuth2AuthorizedClientRepository authz) {

    ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
            new ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, authz);

    return WebClient.builder().filter(oauth2).build();
  }

}
