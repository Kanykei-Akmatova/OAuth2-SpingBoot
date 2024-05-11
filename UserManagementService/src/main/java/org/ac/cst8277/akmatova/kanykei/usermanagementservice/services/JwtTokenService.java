package org.ac.cst8277.akmatova.kanykei.usermanagementservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtTokenService {

  private static String APP_SECURITY = "app.security.";
  private static String ROLES_CLAIM = "roles";

  private final Environment env;
  private final String issuer;
  private final String privateKey;

  private final String audience;

  public JwtTokenService(Environment env) {
    this.env = env;
    issuer = env.getProperty(APP_SECURITY + "issuer");
    privateKey = env.getProperty(APP_SECURITY + "privateKey");
    audience = env.getProperty(APP_SECURITY + "audience");
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    List<String> roles = new ArrayList<>();
    Map<String, Object> rolesClaim = new HashMap<>();
    userDetails.getAuthorities().forEach(a -> roles.add(a.getAuthority()));
    rolesClaim.put(ROLES_CLAIM, roles);

    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setIssuer(issuer)
            .setAudience(audience)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10 minutes
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .addClaims(rolesClaim)
            .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    final String tokenIssuer = extractClaim(token, Claims::getIssuer);
    final String tokenAudience = extractClaim(token, Claims::getAudience);

    return (username.equals(userDetails.getUsername()))
            && issuer.equals(tokenIssuer)
            && audience.equals(tokenAudience)
            && !isTokenExpired(token)
            && hasUserRequiredRole(userDetails, token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(privateKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private boolean hasUserRequiredRole(UserDetails userDetails, String token) {
    Claims extractAllClaims = extractAllClaims(token);
    List<String> tokenRoles = extractAllClaims.get(ROLES_CLAIM, List.class);
    HashSet<String> tokenRolesSet = new HashSet<>(tokenRoles);
    for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
      if (tokenRolesSet.contains(grantedAuthority.getAuthority())) {
        return true;
      }
    }

    return false;
  }
}
