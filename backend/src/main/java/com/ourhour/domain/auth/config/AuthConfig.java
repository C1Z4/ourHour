package com.ourhour.domain.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;


@Configuration
public class AuthConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port; // 포트는 int 타입으로 주입받는 것이 좋음

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

   @Bean
    public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }

   @Bean
    public JavaMailSender javaMailSender() {
       JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
       mailSender.setHost(host);
       mailSender.setPort(port);
       mailSender.setUsername(username);
       mailSender.setPassword(password);

       Properties props = mailSender.getJavaMailProperties();
       props.put("mail.smtp.auth", "true");
       props.put("mail.smtp.starttls.enable", "true");
       props.put("mail.smtp.starttls.required", "true");

      mailSender.setJavaMailProperties(props);

       return mailSender;
   }
}
