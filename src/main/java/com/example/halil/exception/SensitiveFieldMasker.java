package com.example.halil.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 로깅할때 개인정보 혹은 민감한 정보 마스킹하는 클래스 중첩 구조는 마스킹 안됨!
 */
@RequiredArgsConstructor
@Component
public class SensitiveFieldMasker {

    private static final Set<String> SENSITIVE_FIELD_NAMES = Set.of(
            "email", "password"
    );

    private final ObjectMapper objectMapper;

    public String maskJsonString(String json) {
        if (json == null || json.isBlank()) {
            return json;
        }

        try {
            JsonNode root = objectMapper.readTree(json);
            if (!root.isObject()) {
                return json;
            }

            ObjectNode masked = root.deepCopy();

            for (String field : SENSITIVE_FIELD_NAMES) {
                if (masked.has(field)) {
                    masked.put(field, "****");
                }
            }

            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(masked);

        } catch (Exception e) {
            return json; // 마스킹 실패 시 원본 그대로 반환
        }
    }
}
