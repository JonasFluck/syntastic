package org.example;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.util.*;
import org.example.concepts.attributes.Snp;

public class SnpLoader {

    public static Map<String, Map<String, Snp>> loadSnps(String url, String user, String password) {
        Map<String, Map<String, Snp>> patientSnps = new HashMap<>();
        String query =
                "SELECT g.patient_id, g.snp_id, s.chromosome, s.position, " +
                        "s.reference, s.alternative, g.genotype " +
                        "FROM genotype g " +
                        "JOIN snps s ON g.snp_id = s.snp_id " +
                        "WHERE g.patient_id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
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
}