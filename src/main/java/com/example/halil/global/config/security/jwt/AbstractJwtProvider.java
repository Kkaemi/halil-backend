package com.example.halil.global.config.security.jwt;

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

public abstract class AbstractJwtProvider {

    public String generate(JwtClaim claim) {
        try {
            MACSigner signer = new MACSigner(getSecret());

            Instant expirationTime = claim.issuedAt()
                    .plusSeconds(getType().getExpirationSeconds());

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(claim.subject())
                    .claim("role", claim.role())
                    .issueTime(Date.from(claim.issuedAt()))
                    .expirationTime(Date.from(expirationTime))
                    .build();

            SignedJWT signedJwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            signedJwt.sign(signer);

            return signedJwt.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verify(String rawToken) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(rawToken);

            JWTClaimsSet jwtClaimsSet = signedJwt.getJWTClaimsSet();

            Instant expirationTime = jwtClaimsSet.getExpirationTime().toInstant();

            MACVerifier verifier = new MACVerifier(getSecret());
            boolean verified = signedJwt.verify(verifier);

            return expirationTime.isAfter(getNow()) && verified;
        } catch (ParseException | JOSEException e) {
            return false;
        }
    }

    public JwtClaim parse(String rawToken) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(rawToken);

            JWTClaimsSet jwtClaimsSet = signedJwt.getJWTClaimsSet();

            String userId = jwtClaimsSet.getSubject();
            String role = jwtClaimsSet.getClaimAsString("role");
            Instant issuedAt = jwtClaimsSet.getIssueTime().toInstant();

            return new JwtClaim(userId, role, issuedAt);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract TokenType getType();

    protected abstract String getSecret();

    protected abstract Instant getNow();
}
