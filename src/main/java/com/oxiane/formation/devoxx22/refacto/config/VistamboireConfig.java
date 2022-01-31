package com.oxiane.formation.devoxx22.refacto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VistamboireConfig {

    @Bean
    public String information() {
        return "Pouet";
    }

}
