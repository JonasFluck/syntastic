package org.example;

import org.example.concepts.attributes.DrugEvent;
import org.example.concepts.attributes.Patient;
import org.example.concepts.attributes.Snp;

import java.math.BigDecimal;
import java.sql.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExportPatientsDAO {
    private final String url;
    private final String user;
    private final String password;

    public ExportPatientsDAO(String url, String user, String password) {
        // Constructor
        this.url = url;
        this.user = user;
        this.password = password;
    }
    public void Export(List<Patient> patients) {
        ClearTables();
        for (Patient patient : patients) {
            var attributes = patient.getAttributes();
            List<DrugEvent> drugEvents = ((List<?>) attributes.get("drugEvents"))
                    .stream()
                    .filter(DrugEvent.class::isInstance)
                    .map(DrugEvent.class::cast)
                    .toList();
            ExportPatients(patients);
            ExportDrugEvents(Integer.parseInt(patient.getId()),drugEvents);
        }
    }

    private void ClearTables() {
        updatePatientColumnsToNull(url, user, password);
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            var sql = "DELETE FROM drug_events";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while clearing tables in database.");
        }
    }

    private void updatePatientColumnsToNull(String url, String user, String password) {
        String sql = "UPDATE patient SET "
                + "S279_SEIZ_N12_COMB_CAT = NULL, "
                + "S290_SEIZ_REMISS_YES_NO = NULL, "
                + "S305_AED_NON_EPI_SEIZ = NULL, "
                + "diagnosis = NULL, "
                + "number_treatment_episode = NULL, "
                + "S252_SEIZ_NON_EPIL = NULL, "
                + "S253_SEIZ_FEBRILE = NULL, "
                + "S265_SEIZ_NAED_GTC_CAT = NULL, "
                + "S267_SEIZ_NAED_NGTC_CAT = NULL, "
                + "S269_SEIZ_NAED_COMB_CAT = NULL, "
                + "S275_SEIZ_N12_GTC_CAT = NULL, "
                + "S277_SEIZ_N12_NGTC_CAT = NULL, "
                + "S243_SEIZ_TONIC = NULL, "
                + "S244_SEIZ_ATONIC = NULL, "
                + "S245_SEIZ_MYOCLONIC = NULL, "
                + "S246_SEIZ_SIMPL_PART = NULL, "
                + "S247_SEIZ_COMPL_PART = NULL, "
                + "S248_SEIZ_SEC_GTC = NULL, "
                + "S249_SEIZ_UNCLASS_PART = NULL, "
                + "S250_SEIZ_UNCLASS_GTC = NULL, "
                + "S251_SEIZ_UNCERT_EPIL = NULL, "
                + "S180_HIPPOCAMP_SCLEROSIS = NULL, "
                + "S181_HIPPOCAMP_SCLER_LEFT = NULL, "
                + "S182_HIPPOCAMP_SCLER_RIGHT = NULL, "
                + "S188_POSITIVE_FAMHX = NULL, "
                + "S190_NEUROL_PROGR_DISORDER = NULL, "
                + "S200_NEUROL_EXAM_RESULT = NULL, "
                + "S239_SEIZ_PHOTOSENSITIVE = NULL, "
                + "S240_SEIZ_PRIM_GEN_TON_CLON = NULL, "
                + "S241_SEIZ_ABSENCE = NULL, "
                + "S242_SEIZ_CLONIC = NULL, "
                + "D121_NON_ADHERENT = NULL, "
                + "D141_TRIAL_ADEQUATE = NULL, "
                + "S264_SEIZ_NAED_GTC_ABS = NULL, "
                + "S266_SEIZ_NAED_NGTC_ABS = NULL, "
                + "S268_SEIZ_NAED_COMB_ABS = NULL, "
                + "S274_SEIZ_N12_GTC_ABS = NULL, "
                + "S276_SEIZ_N12_NGTC_ABS = NULL, "
                + "S278_SEIZ_N12_COMB_ABS = NULL, "
                + "age_of_onset = NULL, "
                + "age_first_seizure = NULL, "
                + "gender = NULL, "
                + "age = NULL, "
                + "country = NULL";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Execute the update query
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while updating the patients table.");
        }
    }

    private void ExportPatients(List<Patient> patients) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false); // Disable auto-commit for batch processing
            for (Patient patient : patients) {
                updatePatient(conn, patient); // Prepare the update but don't execute immediately
            }
            conn.commit(); // Commit all updates at once
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while inserting patient data into database.");
        }
    }

    private void updatePatient(Connection conn, Patient patient) throws SQLException {
        String sql = "UPDATE patient SET "
                + "S279_SEIZ_N12_COMB_CAT = ?, "
                + "S290_SEIZ_REMISS_YES_NO = ?, "
                + "S305_AED_NON_EPI_SEIZ = ?, "
                + "diagnosis = ?, "
                + "number_treatment_episode = ?, "
                + "S252_SEIZ_NON_EPIL = ?, "
                + "S253_SEIZ_FEBRILE = ?, "
                + "S265_SEIZ_NAED_GTC_CAT = ?, "
                + "S267_SEIZ_NAED_NGTC_CAT = ?, "
                + "S269_SEIZ_NAED_COMB_CAT = ?, "
                + "S275_SEIZ_N12_GTC_CAT = ?, "
                + "S277_SEIZ_N12_NGTC_CAT = ?, "
                + "S243_SEIZ_TONIC = ?, "
                + "S244_SEIZ_ATONIC = ?, "
                + "S245_SEIZ_MYOCLONIC = ?, "
                + "S246_SEIZ_SIMPL_PART = ?, "
                + "S247_SEIZ_COMPL_PART = ?, "
                + "S248_SEIZ_SEC_GTC = ?, "
                + "S249_SEIZ_UNCLASS_PART = ?, "
                + "S250_SEIZ_UNCLASS_GTC = ?, "
                + "S251_SEIZ_UNCERT_EPIL = ?, "
                + "S180_HIPPOCAMP_SCLEROSIS = ?, "
                + "S181_HIPPOCAMP_SCLER_LEFT = ?, "
                + "S182_HIPPOCAMP_SCLER_RIGHT = ?, "
                + "S188_POSITIVE_FAMHX = ?, "
                + "S190_NEUROL_PROGR_DISORDER = ?, "
                + "S200_NEUROL_EXAM_RESULT = ?, "
                + "S239_SEIZ_PHOTOSENSITIVE = ?, "
                + "S240_SEIZ_PRIM_GEN_TON_CLON = ?, "
                + "S241_SEIZ_ABSENCE = ?, "
                + "S242_SEIZ_CLONIC = ?, "
                + "D121_NON_ADHERENT = ?, "
                + "D141_TRIAL_ADEQUATE = ?, "
                + "S264_SEIZ_NAED_GTC_ABS = ?, "
                + "S266_SEIZ_NAED_NGTC_ABS = ?, "
                + "S268_SEIZ_NAED_COMB_ABS = ?, "
                + "S274_SEIZ_N12_GTC_ABS = ?, "
                + "S276_SEIZ_N12_NGTC_ABS = ?, "
                + "S278_SEIZ_N12_COMB_ABS = ?, "
                + "age_of_onset = ?, "
                + "age_first_seizure = ?, "
                + "gender = ?, "
                + "age = ?, "
                + "country = ? "
                + "WHERE patient_id = ?";  // We update by patient_id
        var attributes = patient.getAttributes();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Setting the PreparedStatement parameters using the Map values from attributes
            stmt.setFloat(1, Float.parseFloat(attributes.get("S279_SEIZ_N12_COMB_CAT").toString()));
            stmt.setFloat(2, Float.parseFloat(attributes.get("S290_SEIZ_REMISS_YES_NO").toString()));
            stmt.setFloat(3, Float.parseFloat(attributes.get("S305_AED_NON_EPI_SEIZ").toString()));
            stmt.setString(4, attributes.get("diagnosis").toString());
            stmt.setFloat(5, Float.parseFloat(attributes.get("number_treatment_episode").toString()));
            stmt.setFloat(6, Float.parseFloat(attributes.get("S252_SEIZ_NON_EPIL").toString()));
            stmt.setFloat(7, Float.parseFloat(attributes.get("S253_SEIZ_FEBRILE").toString()));
            stmt.setFloat(8, Float.parseFloat(attributes.get("S265_SEIZ_NAED_GTC_CAT").toString()));
            stmt.setFloat(9, Float.parseFloat(attributes.get("S267_SEIZ_NAED_NGTC_CAT").toString()));
            stmt.setFloat(10, Float.parseFloat(attributes.get("S269_SEIZ_NAED_COMB_CAT").toString()));
            stmt.setFloat(11, Float.parseFloat(attributes.get("S275_SEIZ_N12_GTC_CAT").toString()));
            stmt.setFloat(12, Float.parseFloat(attributes.get("S277_SEIZ_N12_NGTC_CAT").toString()));
            stmt.setFloat(13, Float.parseFloat(attributes.get("S243_SEIZ_TONIC").toString()));
            stmt.setFloat(14, Float.parseFloat(attributes.get("S244_SEIZ_ATONIC").toString()));
            stmt.setFloat(15, Float.parseFloat(attributes.get("S245_SEIZ_MYOCLONIC").toString()));
            stmt.setFloat(16, Float.parseFloat(attributes.get("S246_SEIZ_SIMPL_PART").toString()));
            stmt.setFloat(17, Float.parseFloat(attributes.get("S247_SEIZ_COMPL_PART").toString()));
            stmt.setFloat(18, Float.parseFloat(attributes.get("S248_SEIZ_SEC_GTC").toString()));
            stmt.setFloat(19, Float.parseFloat(attributes.get("S249_SEIZ_UNCLASS_PART").toString()));
            stmt.setFloat(20, Float.parseFloat(attributes.get("S250_SEIZ_UNCLASS_GTC").toString()));
            stmt.setFloat(21, Float.parseFloat(attributes.get("S251_SEIZ_UNCERT_EPIL").toString()));
            stmt.setFloat(22, Float.parseFloat(attributes.get("S180_HIPPOCAMP_SCLEROSIS").toString()));
            stmt.setFloat(23, Float.parseFloat(attributes.get("S181_HIPPOCAMP_SCLER_LEFT").toString()));
            stmt.setFloat(24, Float.parseFloat(attributes.get("S182_HIPPOCAMP_SCLER_RIGHT").toString()));
            stmt.setFloat(25, Float.parseFloat(attributes.get("S188_POSITIVE_FAMHX").toString()));
            stmt.setFloat(26, Float.parseFloat(attributes.get("S190_NEUROL_PROGR_DISORDER").toString()));
            stmt.setFloat(27, Float.parseFloat(attributes.get("S200_NEUROL_EXAM_RESULT").toString()));
            stmt.setFloat(28, Float.parseFloat(attributes.get("S239_SEIZ_PHOTOSENSITIVE").toString()));
            stmt.setFloat(29, Float.parseFloat(attributes.get("S240_SEIZ_PRIM_GEN_TON_CLON").toString()));
            stmt.setFloat(30, Float.parseFloat(attributes.get("S241_SEIZ_ABSENCE").toString()));
            stmt.setFloat(31, Float.parseFloat(attributes.get("S242_SEIZ_CLONIC").toString()));
            stmt.setFloat(32, Float.parseFloat(attributes.get("D121_NON_ADHERENT").toString()));
            stmt.setFloat(33, Float.parseFloat(attributes.get("D141_TRIAL_ADEQUATE").toString()));
            stmt.setString(34, attributes.get("S264_SEIZ_NAED_GTC_ABS").toString());
            stmt.setString(35, attributes.get("S266_SEIZ_NAED_NGTC_ABS").toString());
            stmt.setString(36, attributes.get("S268_SEIZ_NAED_COMB_ABS").toString());
            stmt.setString(37, attributes.get("S274_SEIZ_N12_GTC_ABS").toString());
            stmt.setString(38, attributes.get("S276_SEIZ_N12_NGTC_ABS").toString());
            stmt.setString(39, attributes.get("S278_SEIZ_N12_COMB_ABS").toString());
            stmt.setString(40, attributes.get("age_of_onset").toString());
            stmt.setString(41, attributes.get("age_first_seizure").toString());
            stmt.setString(42, attributes.get("gender").toString());
            stmt.setInt(43, Integer.parseInt(attributes.get("age").toString()));
            stmt.setString(44, attributes.get("country").toString());
            stmt.setInt(45, Integer.parseInt(patient.getId()));  // The patient_id field to identify the record

            // Execute the update query
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while updating the patient record.");
        }
    }

    private void ExportDrugEvents(int patientId, List<DrugEvent> drugEvents) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false); // Disable auto-commit for batch processing
            for (DrugEvent event : drugEvents) {
                insertDrugEvent(conn, patientId, event); // Add the event to the batch
            }
            conn.commit(); // Commit all inserts at once
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while inserting drug event data into database.");
        }
    }

    private void insertDrugEvent(Connection conn,int patientId, DrugEvent event) throws SQLException {
        String sql = "INSERT INTO drug_events (patient_id, drug_name, drug_family, prescription_count, mutation_rate, drug_event_count, response) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            stmt.setString(2, event.getDrug().getName());
            stmt.setString(3, Arrays.toString(event.getDrug().getFamily()));
            stmt.setInt(4, event.getDrug().getPrescriptionFrequency());
            stmt.setBigDecimal(5, BigDecimal.valueOf(event.getSnpDrugMutationRate()));
            stmt.setInt(6, event.getDrugEventCount());
            stmt.setBoolean(7, event.isResponse());

            stmt.executeUpdate();
        }
    }
}
