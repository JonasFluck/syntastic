package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

import org.example.concepts.attributes.Snp;

public class SnpLoader {

    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;

    static {
        Properties props = loadDbProperties();
        DB_URL = props.getProperty("db.url");
        DB_USER = props.getProperty("db.user");
        DB_PASSWORD = props.getProperty("db.password");
    }

    public static Map<String, Map<String, Snp>> loadSnps() {
        Map<String, Map<String, Snp>> patientSnps = new HashMap<>();

        String query =
                "SELECT g.patient_id, g.snp_id, s.chromosome, s.position, " +
                        "s.reference, s.alternative, g.genotype " +
                        "FROM genotype g " +
                        "JOIN snps s ON g.snp_id = s.snp_id";

        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                String patientId = rs.getString("patient_id");
                String snpId = rs.getString("snp_id");
                int chromosome = rs.getInt("chromosome");
                int position = Integer.parseInt(rs.getString("position"));
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

                patientSnps.computeIfAbsent(patientId, k -> new HashMap<>()).put(snpId, snp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while fetching SNP data from database.");
        }

        return patientSnps;
    }

    private static Properties loadDbProperties() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("config/db.properties")) {
            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load DB config.");
        }
        return props;
    }
}
