package com.example.halil.global.config.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;
    private final LoggingInterceptor loggingInterceptor;

    // RestController 어노테이션이 붙은 컨트롤러에만 prefix를 붙여준다.
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(
                "/api",
                c -> c.isAnnotationPresent(RestController.class)
        );
    }

    /**
     * 로깅 인터셉터 설정
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor).addPathPatterns("/**");
    }

    /**
     * XSS 공격 방지 목적으로 응답 데이터 HTML 캐릭터 이스케이프
     */
    @Bean
    public MappingJackson2HttpMessageConverter jsonEscapeConverter() {
        ObjectMapper copy = objectMapper.copy();
        copy.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
        return new MappingJackson2HttpMessageConverter(copy);
    }
}
