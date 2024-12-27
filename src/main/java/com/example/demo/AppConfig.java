package com.example.demo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    @Value("${user.name}")
    private String nome;

    @Value("${user.pass}")
    private String senha;
}