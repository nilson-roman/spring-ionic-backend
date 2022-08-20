package com.udemy.nelio.cursomc.config;

import com.udemy.nelio.cursomc.services.DBService;
import com.udemy.nelio.cursomc.services.EmailService;
import com.udemy.nelio.cursomc.services.SmtpEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.text.ParseException;

@Configuration
@Profile("dev")
public class DevConfig {

    @Autowired
    private DBService dbService;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String stratagy;

    @Bean
    public boolean instantiateDatabase() throws ParseException {

        if(!"create".equals(stratagy)){
            return false;
        }
        dbService.instantiateTestDatabase();
        return true;
    }

    @Bean
    public EmailService emailService() {
        return new SmtpEmailService();
    }
}
