package com.oxiane.formation.devoxx22.refacto.config;

import com.oxiane.formation.devoxx22.refacto.helpers.FacturePrinter;
import com.oxiane.formation.devoxx22.refacto.helpers.PrixUnitCalculateur;
import com.oxiane.formation.devoxx22.refacto.helpers.VerboseDateFormat;
import com.oxiane.formation.devoxx22.refacto.helpers.impl.FacturePrinterImpl;
import com.oxiane.formation.devoxx22.refacto.helpers.impl.PrixUnitCalculateurImpl;
import com.oxiane.formation.devoxx22.refacto.services.business.FactureBusiness;
import com.oxiane.formation.devoxx22.refacto.services.business.impl.FactureBusinessImpl;
import com.oxiane.formation.devoxx22.refacto.services.jdbc.DatabaseValuesExtractor;
import com.oxiane.formation.devoxx22.refacto.services.jpa.spi.DatabaseValuesExtractorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VistamboireConfig {

    @Bean
    public FacturePrinter facturePrinter() { return new FacturePrinterImpl(); }

    @Bean
    public PrixUnitCalculateur prixUnitCalculateur() { return new PrixUnitCalculateurImpl(); }

    @Bean
    public DatabaseValuesExtractor databaseValuesExtractor() { return new DatabaseValuesExtractorImpl(); }

    @Bean
    public VerboseDateFormat apiDateFormatter() { return new VerboseDateFormat("yyyy-MM-dd"); }

    @Bean
    public FactureBusiness factureBusiness() { return new FactureBusinessImpl(); }
}
