package com.example.halil.auth.infra;

import com.example.halil.auth.domain.AuthToken;
import com.example.halil.auth.domain.AuthTokenBundle;
import com.example.halil.auth.domain.ExpirationTime;
import com.example.halil.auth.domain.IssuedAt;
import com.example.halil.auth.domain.JwtService;
import com.example.halil.auth.domain.TokenType;
import com.example.halil.auth.domain.UserInfo;
import com.example.halil.auth.service.AuthErrorCode;
import com.example.halil.properties.JwtProperties;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    @Override
    public AuthTokenBundle generateBundle(UserInfo userInfo, Instant issuedAt) {
        String accessToken = generateToken(userInfo, TokenType.ACCESS, issuedAt);
        String refreshToken = generateToken(userInfo, TokenType.REFRESH, issuedAt);

        return new AuthTokenBundle(accessToken, refreshToken);
    }

    private String generateToken(UserInfo userInfo, TokenType tokenType, Instant issuedAt) {
        MACSigner signer = createSigner(tokenType);
        JWTClaimsSet claimsSet = buildClaims(userInfo, tokenType, issuedAt);
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        signJwt(signedJWT, signer);

        return signedJWT.serialize();
    }

    private MACSigner createSigner(TokenType tokenType) {
        try {
            return switch (tokenType) {
                case ACCESS -> new MACSigner(jwtProperties.getAccessTokenSecret());
                case REFRESH -> new MACSigner(jwtProperties.getRefreshTokenSecret());
            };
        } catch (KeyLengthException e) {
            log.error("시크릿 키 길이는 최소 32바이트여야 합니다", e);
            throw AuthErrorCode.TOKEN_GENERATION_FAILED.exception();
        }
    }

    private JWTClaimsSet buildClaims(UserInfo userInfo, TokenType tokenType, Instant issuedAt) {
        return new JWTClaimsSet.Builder()
                .subject(String.valueOf(userInfo.userId()))
                .claim("role", userInfo.role())
                .claim("type", tokenType.name())
                .issueTime(Date.from(issuedAt))
                .expirationTime(Date.from(tokenType.calculateExpirationTime(issuedAt)))
                .build();
    }

    private void signJwt(SignedJWT jwt, MACSigner signer) {
        try {
            jwt.sign(signer);
        } catch (JOSEException e) {
            log.error("JWS 객체에 서명 할 수 없습니다", e);
            throw AuthErrorCode.TOKEN_GENERATION_FAILED.exception();
        }
    }

    @Override
    public AuthToken parse(String rawToken) {

        SignedJWT signedJwt = parseSignedJwt(rawToken);

        JWTClaimsSet jwtClaimsSet = getJwtClaimSet(signedJwt);
        TokenType tokenType = TokenType.valueOf(jwtClaimsSet.getClaim("type").toString());
        IssuedAt issuedAt = new IssuedAt(jwtClaimsSet.getIssueTime().toInstant());
        ExpirationTime expirationTime = new ExpirationTime(jwtClaimsSet.getExpirationTime().toInstant());
        long userId = Long.parseLong(jwtClaimsSet.getSubject());
        String role = String.valueOf(jwtClaimsSet.getClaim("role"));

        MACVerifier verifier = createVerifier(tokenType);
        boolean verified = verifySignedJwt(signedJwt, verifier);

        return new AuthToken(
                rawToken, tokenType,
                issuedAt, expirationTime,
                new UserInfo(userId, role),
                verified
        );
    }

    private SignedJWT parseSignedJwt(String token) {
        if (token == null) {
            throw AuthErrorCode.INVALID_TOKEN.exception();
        }

        try {
            return SignedJWT.parse(token);
        } catch (ParseException e) {
            log.error("JWT를 파싱할 수 없습니다", e);
            throw AuthErrorCode.INVALID_TOKEN.exception();
        }
    }

    private JWTClaimsSet getJwtClaimSet(SignedJWT signedJwt) {
        try {
            return signedJwt.getJWTClaimsSet();
        } catch (ParseException e) {
            log.error("JWT 클레임 추출에 실패했습니다", e);
            throw AuthErrorCode.INVALID_TOKEN.exception();
        }
    }

    private MACVerifier createVerifier(TokenType tokenType) {
        try {
            return switch (tokenType) {
                case ACCESS -> new MACVerifier(jwtProperties.getAccessTokenSecret());
                case REFRESH -> new MACVerifier(jwtProperties.getRefreshTokenSecret());
            };
        } catch (JOSEException e) {
            log.error("시크릿 키 길이는 최소 32바이트여야 합니다", e);
            throw AuthErrorCode.TOKEN_GENERATION_FAILED.exception();
        }
    }

    private boolean verifySignedJwt(SignedJWT signedJwt, MACVerifier verifier) {
        try {
            return signedJwt.verify(verifier);
        } catch (JOSEException e) {
            log.error("JWS 객체를 검증 할 수 없습니다", e);
            throw AuthErrorCode.TOKEN_GENERATION_FAILED.exception();
        }
    }
}
