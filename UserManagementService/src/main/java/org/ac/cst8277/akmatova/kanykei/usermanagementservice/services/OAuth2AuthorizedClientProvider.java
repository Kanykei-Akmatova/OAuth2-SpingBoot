package org.ac.cst8277.akmatova.kanykei.usermanagementservice.services;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AuthorizedClientProvider {

    private final OAuth2AuthorizedClientService clientService;

    public OAuth2AuthorizedClientProvider(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    public OAuth2AuthorizedClient getClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        return clientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
    }
}