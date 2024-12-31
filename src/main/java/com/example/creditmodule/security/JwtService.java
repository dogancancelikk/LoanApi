package com.example.creditmodule.security;


import com.example.creditmodule.entity.CustomerEntity;
import com.example.creditmodule.entity.UserEntity;
import com.example.creditmodule.entity.lookup.UserRole;
import com.example.creditmodule.exception.ApiException;
import com.example.creditmodule.response.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtService {
    @Value("${application.jwt.secret}")
    private String secretKey;

    @Value("${application.jwt.expiration}")
    private long jwtExpiration;


    public String getUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UUID getCustomerId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String jwtToken = authHeader.substring(7);
        Claims claims = extractAllClaims(jwtToken);
        return UUID.fromString((String) claims.get("customer_id"));

    }
    public void validateAuthorization(HttpServletRequest request, UUID customerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority
                        .getAuthority().equals(UserRole.ROLE_ADMIN.name()));
        if (isAdmin) {
            return;
        }
        UUID customerIdInToken = getCustomerId(request);
        if (!customerIdInToken.equals(customerId)) {
            throw new ApiException("Customer is not authorized to access this resource", HttpStatus.FORBIDDEN);
        }
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        String username = getUsername(jwtToken);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(jwtToken);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public TokenResponse generateTokenForCustomer(CustomerEntity customerEntity) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("customer_id", customerEntity.getId());
        String jwtToken = buildToken(claims, customerEntity.getUser(), jwtExpiration);
        return TokenResponse
                .builder()
                .expiresIn(jwtExpiration)
                .accessToken(jwtToken)
                .build();
    }

    public TokenResponse generateTokenForAdmin(UserEntity userEntity) {
        String jwtToken =  buildToken(new HashMap<>(), userEntity, jwtExpiration);
        return TokenResponse
                .builder()
                .expiresIn(jwtExpiration)
                .accessToken(jwtToken)
                .build();
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
