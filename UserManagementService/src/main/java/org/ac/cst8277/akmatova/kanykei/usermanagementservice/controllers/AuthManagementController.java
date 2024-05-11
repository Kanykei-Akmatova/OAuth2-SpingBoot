package org.ac.cst8277.akmatova.kanykei.usermanagementservice.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.configuration.AuthConfigurationConstants;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.services.JwtTokenService;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.services.OAuth2AuthorizedClientProvider;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.services.UserManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RestController
public class AuthManagementController {
    private static final Logger logger = LogManager.getLogger(AuthManagementController.class);
    private static final String GITHUB_BASE_URL =  "https://api.github.com";
    private UserManagementService userManagementService;
    private JwtTokenService jwtService;
    private OAuth2AuthorizedClientProvider oauth2AuthorizedClientProvider;

    public AuthManagementController(){
    }

    @Autowired
    public AuthManagementController(UserManagementService userManagementService,
                                    JwtTokenService jwtService,
                                    OAuth2AuthorizedClientProvider oauth2AuthorizedClientProvider
    ) {
        this.userManagementService = userManagementService;
        this.jwtService = jwtService;
        this.oauth2AuthorizedClientProvider = oauth2AuthorizedClientProvider;
    }

    @GetMapping(path = "/access/token", produces = "application/json")
    public ResponseEntity<ApiResponse> accessToken() {
        logger.info("login Callback happen.");

        try {
            String accessToken = oauth2AuthorizedClientProvider.getClient().getAccessToken().getTokenValue();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            WebClient webClient1 = WebClient.builder()
                    .baseUrl(GITHUB_BASE_URL)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            List<Map<String, Object>> emails = webClient1.get()
                    .uri("/user/emails")
                    .headers(h -> h.setBearerAuth(accessToken))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .exchange()
                    .block()
                    .bodyToMono(List.class)
                    .block();

            String username = emails.get(0).get("email").toString();
            UserDetails userDetails = userManagementService.findByEmail(username);

            String jwtToken = jwtService.generateToken(userDetails);

            logger.info("===>>> (Authorized) Authentication: " + authentication);

            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(200)
                    .setMessage("access_token")
                    .setData(jwtToken)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception exception) {

            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(500)
                    .setMessage(exception.toString())
                    .setData(null)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/authenticate", produces = "application/json")
    public ResponseEntity<AuthResponse> authenticate(@RequestHeader(
            AuthConfigurationConstants.HEADER_AUTHORIZATION) String accessToken) {
        try {
            String jwt = accessToken.substring(7);
            logger.info("Authentication with token: " + jwt);
            String userName = jwtService.extractUsername(jwt);
            UserDetails userDetails = userManagementService.findByEmail(userName);

            boolean isTokenValid = jwtService.isTokenValid(jwt, userDetails);
            AuthResponse authResponse;

            if(isTokenValid) {
                authResponse = new AuthResponse
                        .Builder()
                        .setCode(200)
                        .setStatus("AUTHORIZED")
                        .setUser(userName)
                        .build();
                return new ResponseEntity<>(authResponse, HttpStatus.OK);
            } else {
                authResponse = new AuthResponse
                        .Builder()
                        .setCode(401)
                        .setStatus("UNAUTHORIZED")
                        .setUser(null)
                        .build();
                return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
            }
        } catch (SignatureException | ExpiredJwtException signatureException) {

            AuthResponse authResponse = new AuthResponse
                    .Builder()
                    .setCode(401)
                    .setStatus("Access token is not valid.")
                    .setUser(null)
                    .build();

            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception exception) {

            AuthResponse authResponse = new AuthResponse
                    .Builder()
                    .setCode(500)
                    .setStatus(exception.toString())
                    .setUser(null)
                    .build();

            return new ResponseEntity<>(authResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
