package org.example;

import java.sql.*;
import java.util.*;

import org.example.concepts.attributes.Snp;

public class SnpLoader {

    // Database connection settings
    private static final String DB_URL = "jdbc:mysql://ibmidb.cs.uni-tuebingen.de:3306/syntastic";
    private static final String DB_USER = "braiting";
    private static final String DB_PASSWORD = "tarElIf4";

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
                String genotype = rs.getString("genotype"); // this is the expression in your original

                // Create SNP object
                Snp snp = new Snp();
                snp.setRsId(snpId);
                snp.setChromosome(chromosome);
                snp.setPosition(position);
                snp.setRef(reference);
                snp.setAlt(alternative);
                snp.setExpression(genotype);

                // Add to patient-specific SNP map
                patientSnps.computeIfAbsent(patientId, k -> new HashMap<>()).put(snpId, snp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while fetching SNP data from database.");
        }

        return patientSnps;
    }
}
