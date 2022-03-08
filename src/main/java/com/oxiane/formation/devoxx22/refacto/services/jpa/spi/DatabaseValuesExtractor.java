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
            Calendar lowerBondCalendar = (Calendar)date.clone();
            lowerBondCalendar.add(Calendar.YEAR, -1);
            Date startOfYear = new Date(lowerBondCalendar.getTimeInMillis());
            Date startOfNextYear = new Date(date.getTimeInMillis());
            PreparedStatement ps = connection.prepareStatement("""
                    select sum(QTE) from FACTURE 
                    where CLIENT_ID=? and DATE>=? and DATE<?
                    """);
            ps.setLong(1, clientId);
            ps.setDate(2, startOfYear);
            ps.setDate(3, startOfNextYear);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int qte = rs.getInt(1);
            return rs.wasNull() ? 0 : qte;
        } catch (SQLException ex) {
            LOGGER.error("getQuantiteDejaCommandeeCetteAnnee({},{})", ex, clientId, date);
            return 0;
        }
    }
}
