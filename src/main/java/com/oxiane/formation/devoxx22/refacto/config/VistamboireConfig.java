package com.oxiane.formation.devoxx22.refacto.config;

import com.oxiane.formation.devoxx22.refacto.helpers.FacturePrinter;
import com.oxiane.formation.devoxx22.refacto.helpers.impl.FacturePrinterImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VistamboireConfig {

    @Bean
    public FacturePrinter facturePrinter() { return new FacturePrinterImpl(); }
}
