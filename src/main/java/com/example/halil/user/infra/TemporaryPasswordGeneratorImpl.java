package com.example.halil.user.infra;

import com.example.halil.user.domain.TemporaryPasswordGenerator;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TemporaryPasswordGeneratorImpl implements TemporaryPasswordGenerator {

    private static final int DEFAULT_LENGTH = 10;

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = UPPER.toLowerCase();
    private static final String DIGITS = "0123456789";
    private static final String SPECIALS = "!#$&";
    private static final String ALL_CHARS = UPPER + LOWER + DIGITS + SPECIALS;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String generate() {

        List<Character> characterList = new LinkedList<>();

        // 필수로 포함할 문자들 추가
        characterList.add(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        characterList.add(SPECIALS.charAt(RANDOM.nextInt(SPECIALS.length())));

        // 나머지 자리 채우기 (길이만큼)
        for (int i = characterList.size(); i < DEFAULT_LENGTH; i++) {
            characterList.add(ALL_CHARS.charAt(RANDOM.nextInt(ALL_CHARS.length())));
        }

        // 순서 섞어서 필수 문자 위치를 랜덤화
        Collections.shuffle(characterList, RANDOM);

        // 문자열로 변환하여 반환
        StringBuilder sb = new StringBuilder(DEFAULT_LENGTH);
        for (char c : characterList) {
            sb.append(c);
        }

        return sb.toString();
    }
}
