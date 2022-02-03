package com.oxiane.formation.devoxx22.refacto.services.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DatabaseStructUpdater implements BeanPostProcessor {
    public static final String SCRIPT_ROOT = "/com/oxiane/formation/devoxx22/refacto/services/update/";
    /**
     * Scripts that must be run on database when application starts
     */
    public static final String[] SCRIPTS = {
            "facture-qte.sql"
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseStructUpdater.class);
    public static final String DB_STRUCT_DDL = """
            create table if not exists db_struct (
                script varchar(250) not null unique,
                run_date timestamp not null)
            """;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource) {
            updateDatabaseStruct((DataSource) bean);
        }
        return bean;
    }

    private void updateDatabaseStruct(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(DB_STRUCT_DDL)) {
                ps.executeUpdate();
                connection.commit();
                applyPatches(connection);
            }
        } catch (Exception exception) {
            throw new RuntimeException("While updating database struct", exception);
        }
    }

    private void applyPatches(Connection connection) throws SQLException, IOException {
        for (String scriptName : SCRIPTS) {
            runScript(connection, scriptName);
        }
    }

    private void runScript(Connection con, String scriptName) throws SQLException {
        if (canRunScript(con, scriptName)) {
            LOGGER.info("Running script {}", scriptName);
            try {
                try (PreparedStatement ps = con.prepareStatement("insert into db_struct(script, run_date) values (?, now())")) {
                    ps.setString(1, scriptName);
                    boolean somethingHasBeenRun = false;
                    for (String order : getOrders(scriptName)) {
                        try (PreparedStatement ps2 = con.prepareStatement(order)) {
                            ps2.executeUpdate();
                            somethingHasBeenRun = true;
                        }
                    }
                    if (somethingHasBeenRun) {
                        ps.executeUpdate();
                        con.commit();
                    } else {
                        LOGGER.warn("Unable to run SQL script {}, no order found in", scriptName);
                        con.rollback();
                    }
                }
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public static List<String> getOrders(String scriptName) {
        List<String> ret = new ArrayList<>();
        try (
                InputStream is = DatabaseStructUpdater.class.getResourceAsStream(SCRIPT_ROOT + scriptName);
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String delimiter = ";";
            String line = br.readLine();
            while (line != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("#")) {
                    // on ignore la ligne
                } else if (trimmed.isEmpty()) {
                    // on ignore la ligne
                } else if (trimmed.toUpperCase().startsWith("DELIMITER ")) {
                    delimiter = trimmed.toUpperCase().substring(10);
                } else if (trimmed.endsWith(delimiter)) {
                    sb.append(trimmed);
                    // on enlève le delimiter
                    for (int i = 0; i < delimiter.length(); i++) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    ret.add(sb.toString().trim());
                    sb = new StringBuilder();
                } else {
                    sb.append(trimmed).append(System.lineSeparator());
                }
                line = br.readLine();
            }
            if (sb.length() > 0) {
                // we beleive the last order is not terminated by a semi-colon
                // remove the lineSeparator
                for (int i = 0; i < System.lineSeparator().length(); i++) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                ret.add(sb.toString().trim());
            }
            return ret;
        } catch (IOException ex) {
            LOGGER.error("while running " + scriptName, ex);
            return Collections.emptyList();
        } catch (NullPointerException ex) {
            LOGGER.error("script {} not found in {}", scriptName, SCRIPT_ROOT);
            return Collections.emptyList();
        }
    }

    private static boolean canRunScript(Connection con, String scriptName) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select 1 from db_struct where script=?")) {
            ps.setString(1, scriptName);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next();
            }
        }
    }
}
