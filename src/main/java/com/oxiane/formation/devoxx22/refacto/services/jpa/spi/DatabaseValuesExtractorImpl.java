package com.oxiane.formation.devoxx22.refacto.services.jpa.spi;

import com.oxiane.formation.devoxx22.refacto.model.SecteurGeographique;
import com.oxiane.formation.devoxx22.refacto.services.jdbc.DatabaseValuesExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Calendar;

public class DatabaseValuesExtractorImpl implements DatabaseValuesExtractor {
    @Autowired
    DataSource dataSource;
    private Logger LOGGER = LoggerFactory.getLogger(DatabaseValuesExtractorImpl.class);

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

    public SecteurGeographique getSecteurGeographiqueByDepartement(String departement) {
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT SG.NOM, SG.COEF_MULTI FROM SECTEUR_GEOGRAPHIQUE SG, DEPARTEMENT D WHERE D.SECTEUR_GEO=SG.ID AND D.CODE=?");
            ps.setString(1, departement);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return new SecteurGeographique(rs.getString(1), rs.getBigDecimal(2));
            }
        } catch(SQLException ex) {
            LOGGER.error("getSecteurGeographiqueByDepartement({})", ex, departement);
        }
        return null;
    }
}
