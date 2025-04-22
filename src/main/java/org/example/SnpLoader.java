package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

import org.example.concepts.attributes.Snp;

public class SnpLoader {

    public static Map<String, Map<String, Snp>> loadSnps(String configPath) {
        Properties props = loadDbProperties(configPath);
        String DB_URL = props.getProperty("db.url");
        String DB_USER = props.getProperty("db.user");
        String DB_PASSWORD = props.getProperty("db.password");

        Map<String, Map<String, Snp>> patientSnps = new HashMap<>();
        String query =
                "SELECT g.patient_id, g.snp_id, s.chromosome, s.position, " +
                        "s.reference, s.alternative, g.genotype " +
                        "FROM genotype g " +
                        "JOIN snps s ON g.snp_id = s.snp_id " +
                        "WHERE g.patient_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int patientId = 1; patientId <= 5; patientId++) {
                stmt.setInt(1, patientId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String snpId = rs.getString("snp_id");
                        int chromosome = rs.getInt("chromosome");
                        int position = rs.getInt("position");
                        char reference = rs.getString("reference").charAt(0);
                        char alternative = rs.getString("alternative").charAt(0);
                        String genotype = rs.getString("genotype");

                        Snp snp = new Snp();
                        snp.setRsId(snpId);
                        snp.setChromosome(chromosome);
                        snp.setPosition(position);
                        snp.setRef(reference);
                        snp.setAlt(alternative);
                        snp.setExpression(genotype);

                        patientSnps.computeIfAbsent(String.valueOf(patientId), k -> new HashMap<>()).put(snpId, snp);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while fetching SNP data from database.");
        }

        return patientSnps;
    }

    private static Properties loadDbProperties(String configPath) {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(configPath)) {
            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load DB config from: " + configPath);
        }
        return props;
    }
}
