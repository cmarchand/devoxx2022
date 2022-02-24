package com.oxiane.formation.devoxx22.refacto.services.jpa.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Calendar;

@Component
public class DatabaseValuesExtractor {
    @Autowired
    DataSource dataSource;
    private Logger LOGGER = LoggerFactory.getLogger(DatabaseValuesExtractor.class);

    public int getQuantiteDejaCommandeeCetteAnnee(Long clientId, Calendar date) {
        try(Connection connection = dataSource.getConnection()) {
            Date startOfYear = new Date(date.get(Calendar.YEAR), 0, 1);
            Date startOfNextYear = new Date(date.get(Calendar.YEAR)+1, 0, 1);
            PreparedStatement ps = connection.prepareStatement("""
                    select sum(QTE) from FACTURE 
                    where CLIENT_ID=? and DATE>=? and DATE<?
                    """);
            ps.setLong(1, clientId);
            ps.setDate(2, startOfYear);
            ps.setDate(3, startOfNextYear);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException ex) {
            LOGGER.error("getQuantiteDejaCommandeeCetteAnnee({},{})", ex, clientId, date);
            return 0;
        }
    }
}
