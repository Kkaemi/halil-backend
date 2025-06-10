package com.example.halil.global.config.security.jwt;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JwtProviderFactory {

    private final Map<TokenType, AbstractJwtProvider> jwtProviderMap = new EnumMap<>(TokenType.class);

    public JwtProviderFactory(List<AbstractJwtProvider> abstractJwtProviderList) {
        for (AbstractJwtProvider abstractJwtProvider : abstractJwtProviderList) {
            jwtProviderMap.put(abstractJwtProvider.getType(), abstractJwtProvider);
        }
    }

    public AbstractJwtProvider accessToken() {
        return jwtProviderMap.get(TokenType.ACCESS);
    }

    public AbstractJwtProvider refreshToken() {
        return jwtProviderMap.get(TokenType.REFRESH);
    }
}
