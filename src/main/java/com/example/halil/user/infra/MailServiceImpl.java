package com.example.halil.user.infra;

import com.example.halil.auth.service.AuthErrorCode;
import com.example.halil.properties.CommonProperties;
import com.example.halil.user.service.MailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@RequiredArgsConstructor
@Component
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;
    private final CommonProperties commonProperties;

    @Async
    @Override
    public void sendPasswordResetMail(String to, String temporaryPassword) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        Context context = new Context();
        context.setVariable("tempPassword", temporaryPassword);
        context.setVariable("baseUrl", commonProperties.getBaseUrl());
        String emailContent = springTemplateEngine.process("password-reset", context);

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                    mimeMessage,
                    false,
                    "UTF-8"
            );

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject("❗️[할일] 임시 비밀번호 발급 메일입니다.");
            mimeMessageHelper.setText(emailContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.warn("메일 전송에 실패했습니다", e);
            throw AuthErrorCode.MAIL_DELIVERY_FAILED.exception();
        }
    }
}
