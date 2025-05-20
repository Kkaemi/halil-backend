package com.example.halil.auth;

import com.example.halil.auth.exception.ClaimExtractionException;
import com.example.halil.auth.exception.TokenGenerationException;
import com.example.halil.auth.exception.TokenVerificationException;
import com.example.halil.properties.JwtProperties;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(long userId) {
        // 1 hour
        long accessTokenExpirationTime = 1000L * 60 * 60;

        return generateJwt(userId, accessTokenExpirationTime);
    }

    public String generateRefreshToken(long userId) {
        // 1 month
        long refreshTokenExpirationTime = 1000L * 60 * 60 * 24 * 30;

        return generateJwt(userId, refreshTokenExpirationTime);
    }

    private String generateJwt(long userId, long expirationTime) {
        try {
            MACSigner signer = new MACSigner(jwtProperties.getSecret());

            JWTClaimsSet claimsSet = new Builder()
                    .subject(String.valueOf(userId))
                    .issueTime(new Date())
                    .expirationTime(new Date(new Date().getTime() + expirationTime))
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            log.error("토큰 생성 실패", e);
            throw new TokenGenerationException();
        }
    }

    public boolean verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.verify(new MACVerifier(jwtProperties.getSecret()));
        } catch (Exception e) {
            log.error("토큰 유효성 검사를 실행 할 수 없습니다", e);
            throw new TokenVerificationException();
        }
    }

    public long getUserIdFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return Long.parseLong(signedJWT.getJWTClaimsSet().getSubject());
        } catch (Exception e) {
            log.error("토큰 클레임 분석 실패", e);
            throw new ClaimExtractionException();
        }
    }
}
