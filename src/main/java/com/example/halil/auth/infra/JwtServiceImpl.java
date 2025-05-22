package com.example.halil.auth.infra;

import com.example.halil.auth.domain.AuthToken;
import com.example.halil.auth.domain.AuthTokenBundle;
import com.example.halil.auth.domain.ExpirationTime;
import com.example.halil.auth.domain.IssuedAt;
import com.example.halil.auth.domain.JwtParams;
import com.example.halil.auth.domain.JwtService;
import com.example.halil.auth.domain.JwtType;
import com.example.halil.auth.domain.TokenType;
import com.example.halil.auth.domain.UserInfo;
import com.example.halil.auth.exception.AuthErrorCode;
import com.example.halil.properties.JwtProperties;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
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
    public String generateJwt(JwtParams dto) {
        try {
            MACSigner signer = switch (dto.jwtType()) {
                case ACCESS -> new MACSigner(jwtProperties.getAccessTokenSecret());
                case REFRESH -> new MACSigner(jwtProperties.getRefreshTokenSecret());
            };

            long expirationTimeMillis = dto.expirationTimeMillis() == null
                    ? dto.jwtType().getExpirationTime()
                    : dto.expirationTimeMillis();

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(String.valueOf(dto.userId()))
                    .claim("role", dto.role())
                    .claim("type", dto.jwtType().name())
                    .issueTime(new Date())
                    .expirationTime(new Date(new Date().getTime() + expirationTimeMillis))
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            log.error("토큰 생성 실패", e);
            throw AuthErrorCode.TOKEN_GENERATION_FAILED.asException();
        }
    }

    @Override
    public boolean verifyToken(String token) {
        if (token == null) {
            throw AuthErrorCode.INVALID_TOKEN.asException();
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JwtType jwtType = JwtType.valueOf(signedJWT.getJWTClaimsSet().getClaimAsString("type"));
            MACVerifier verifier = switch (jwtType) {
                case ACCESS -> new MACVerifier(jwtProperties.getAccessTokenSecret());
                case REFRESH -> new MACVerifier(jwtProperties.getRefreshTokenSecret());
            };
            return signedJWT.verify(verifier);
        } catch (Exception e) {
            log.error("토큰 유효성 검사를 실행 할 수 없습니다", e);
            throw AuthErrorCode.TOKEN_VERIFICATION_FAILED.asException();
        }
    }

    @Override
    public long getUserIdFromToken(String token) {
        return Long.parseLong(getClaimsSetFromToken(token).getSubject());
    }

    @Override
    public String getUserRoleFromToken(String token) {
        return String.valueOf(getClaimsSetFromToken(token).getClaim("role"));
    }

    private JWTClaimsSet getClaimsSetFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet();
        } catch (Exception e) {
            log.error("토큰 클레임 분석 실패", e);
            throw AuthErrorCode.CLAIM_EXTRACTION_FAILED.asException();
        }
    }

    @Override
    public AuthTokenBundle generateBundleBy(UserInfo userInfo) {
        Instant now = Instant.now();
        String accessToken = generateToken(
                jwtProperties.getAccessTokenSecret(),
                userInfo.userId(), userInfo.role(),
                TokenType.ACCESS, now
        );
        String refreshToken = generateToken(
                jwtProperties.getRefreshTokenSecret(),
                userInfo.userId(), userInfo.role(),
                TokenType.REFRESH, now
        );
        return new AuthTokenBundle(accessToken, refreshToken);
    }

    private String generateToken(
            String secret,
            long userId,
            String role,
            TokenType tokenType,
            Instant now
    ) {
        try {
            MACSigner signer = new MACSigner(secret);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(String.valueOf(userId))
                    .claim("role", role)
                    .claim("type", tokenType.name())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(tokenType.calculateExpirationTime(now)))
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            log.error("토큰 생성 실패", e);
            throw AuthErrorCode.TOKEN_GENERATION_FAILED.asException();
        }
    }

    @Override
    public AuthToken parse(String token) {
        if (token == null) {
            throw AuthErrorCode.INVALID_TOKEN.asException();
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
            TokenType tokenType = TokenType.valueOf(jwtClaimsSet.getClaimAsString("type"));
            IssuedAt issuedAt = new IssuedAt(jwtClaimsSet.getIssueTime().toInstant());
            ExpirationTime expirationTime = new ExpirationTime(jwtClaimsSet.getExpirationTime().toInstant());
            long userId = Long.parseLong(getClaimsSetFromToken(token).getSubject());
            String role = String.valueOf(getClaimsSetFromToken(token).getClaim("role"));

            MACVerifier verifier = switch (tokenType) {
                case ACCESS -> new MACVerifier(jwtProperties.getAccessTokenSecret());
                case REFRESH -> new MACVerifier(jwtProperties.getRefreshTokenSecret());
            };

            boolean verified = signedJWT.verify(verifier);

            return new AuthToken(
                    token, tokenType,
                    issuedAt, expirationTime,
                    userId, role,
                    verified
            );
        } catch (Exception e) {
            log.error("토큰 파싱 실패", e);
            throw AuthErrorCode.TOKEN_PARSING_FAILED.asException();
        }
    }
}
