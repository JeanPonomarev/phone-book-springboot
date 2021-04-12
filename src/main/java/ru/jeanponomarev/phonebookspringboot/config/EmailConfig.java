package ru.jeanponomarev.phonebookspringboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "email")
public class EmailConfig {

    private String sender;
    private String password;
    private String recipient;
}
