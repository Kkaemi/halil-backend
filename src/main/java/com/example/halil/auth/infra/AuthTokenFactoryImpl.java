package com.example.halil.auth.infra;

import com.example.halil.auth.domain.AuthToken;
import com.example.halil.auth.domain.AuthTokenFactory;
import com.example.halil.auth.domain.ExpirationTime;
import com.example.halil.auth.domain.IssuedAt;
import com.example.halil.auth.domain.TokenType;
import com.example.halil.auth.domain.UserInfo;
import com.example.halil.global.config.properties.JwtProperties;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthTokenFactoryImpl implements AuthTokenFactory {

    private final JwtProperties jwtProperties;
    private final TimeProvider timeProvider;

    @Override
    public AuthToken generateAccessToken(UserInfo userInfo) {
        Instant now = timeProvider.now();
        String rawToken = generateRawToken(
                userInfo,
                jwtProperties.getAccessTokenSecret(),
                now,
                TokenType.ACCESS.calculateExpirationTime(now)
        );

        return new AuthToken(
                rawToken,
                TokenType.ACCESS,
                new IssuedAt(now),
                new ExpirationTime(TokenType.ACCESS.calculateExpirationTime(now)),
                userInfo,
                true
        );
    }

    @Override
    public AuthToken parseAccessToken(String rawToken) {
        return parse(rawToken, TokenType.ACCESS, jwtProperties.getAccessTokenSecret());
    }

    @Override
    public AuthToken generateRefreshToken(UserInfo userInfo) {
        Instant now = timeProvider.now();
        String rawToken = generateRawToken(
                userInfo,
                jwtProperties.getRefreshTokenSecret(),
                now,
                TokenType.REFRESH.calculateExpirationTime(now)
        );

        return new AuthToken(
                rawToken,
                TokenType.REFRESH,
                new IssuedAt(now),
                new ExpirationTime(TokenType.REFRESH.calculateExpirationTime(now)),
                userInfo,
                true
        );
    }

    @Override
    public AuthToken parseRefreshToken(String rawToken) {
        return parse(rawToken, TokenType.REFRESH, jwtProperties.getRefreshTokenSecret());
    }

    private String generateRawToken(
            UserInfo userInfo,
            String secretString,
            Instant issuedAt,
            Instant expirationTime
    ) {
        try {
            MACSigner signer = new MACSigner(secretString);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(String.valueOf(userInfo.userId()))
                    .claim("role", userInfo.role())
                    .issueTime(Date.from(issuedAt))
                    .expirationTime(Date.from(expirationTime))
                    .build();

            SignedJWT signedJwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            signedJwt.sign(signer);

            return signedJwt.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private AuthToken parse(String rawString, TokenType tokenType, String secretString) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(rawString);
            JWTClaimsSet jwtClaimsSet = signedJwt.getJWTClaimsSet();
            long userId = Long.parseLong(jwtClaimsSet.getSubject());
            String role = jwtClaimsSet.getClaimAsString("role");
            Instant issuedAt = jwtClaimsSet.getIssueTime().toInstant();
            Instant expirationTime = jwtClaimsSet.getExpirationTime().toInstant();

            MACVerifier verifier = new MACVerifier(secretString);
            boolean verified = signedJwt.verify(verifier);

            return new AuthToken(
                    rawString,
                    tokenType,
                    new IssuedAt(issuedAt),
                    new ExpirationTime(expirationTime),
                    new UserInfo(userId, role),
                    verified
            );
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}
