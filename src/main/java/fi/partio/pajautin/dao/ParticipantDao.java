package fi.partio.pajautin.dao;

import fi.partio.pajautin.pojos.Participant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipantDao {

    public static Participant getParticipant(String id) {
        Participant participant = new Participant();

        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, first_name, last_name FROM participants WHERE id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                participant.setId(rs.getString(1));
                participant.setFirstName(rs.getString(2));
                participant.setLastName(rs.getString(3));
                return participant;
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean savePreferences(String participantId, List<Integer> preferences) {

        try (Connection con = DataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM program_preferences WHERE participant_id = ?");
        ) {
            ps.setString(1, participantId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO program_preferences (participant_id, workshop_id,pref_order) VALUES " + preferences.stream().map(p -> "(?,?,?)").reduce((a, b) -> a + "," + b).get();
        try (Connection con = DataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int i = 1;
            int j = 1;
            for (Integer preference : preferences) {
                ps.setString(i++, participantId);
                ps.setInt(i++, preference);
                ps.setInt(i++, j++);
            }
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


    }

    public static List<Integer> loadPreferences(String participantId) throws SQLException {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT workshop_id FROM program_preferences WHERE participant_id = ? ORDER BY pref_order")) {
            ps.setString(1, participantId);
            ResultSet rs = ps.executeQuery();
            ArrayList<Integer> preferences = new ArrayList<>();
            while (rs.next()) {
                preferences.add(rs.getInt(1));
            }
            return preferences;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static boolean savePresence(String guid, List<Boolean> presence) {
        if (presence.size() != 3)
            return false;

        try (Connection con = DataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE participants SET present_slot_1 = ?, present_slot_2=?, present_slot_3=? WHERE id=?");
        ) {
            ps.setInt(1, presence.get(0) ? 1 : 0);
            ps.setInt(2, presence.get(1) ? 1 : 0);
            ps.setInt(3, presence.get(2) ? 1 : 0);
            ps.setString(4, guid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<Boolean> loadPresence(String guid) {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT present_slot_1, present_slot_2, present_slot_3 FROM participants WHERE id = ?")) {
            ps.setString(1, guid);
            ResultSet rs = ps.executeQuery();
            ArrayList<Boolean> presence = new ArrayList<>();
            if (rs.next()) {
                presence.add(rs.getInt(1) == 1);
                presence.add(rs.getInt(2) == 1);
                presence.add(rs.getInt(3) == 1);
            }
            return presence;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Integer> loadProgramRegistration(String guid) {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT slot,program_id FROM participant_registration WHERE participant_id = ?")) {
            ps.setString(1, guid);
            ResultSet rs = ps.executeQuery();
            ArrayList<Integer> programRegistration = new ArrayList<>();
            programRegistration.add(null);
            programRegistration.add(null);
            programRegistration.add(null);
            while (rs.next()) {
                programRegistration.set(rs.getInt(1) - 1, rs.getInt(2));
            }
            return programRegistration;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String registerToProgram(String guid, int slot, int programId) {

        // Make sure that participant is not already registered to this slot
        List<Integer> programRegistration = loadProgramRegistration(guid);
        if (programRegistration.get(slot - 1) != null) {
            return "Olet jo ilmoittautunut tämän aikavälin ohjelmaan.";
        }

        // Make sure that participant is present in the time slot
        List<Boolean> presence = loadPresence(guid);
        if (!presence.get(slot - 1)) {
            return "Olet poissa tällä aikavälillä, et voi ilmoittautua ohjelmaan.";
        }

        // Make sure that there is room in the program
        int participantsInProgram = 0;
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM participant_registration as p WHERE slot = ? AND program_id = ?")) {
            ps.setInt(1, slot);
            ps.setInt(2, programId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                participantsInProgram = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Tietokantavirhe.";
        }

        try(Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT maxSize FROM program WHERE id = ?")) {
            ps.setInt(1, programId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int maxParticipants = rs.getInt(1);
                if (participantsInProgram >= maxParticipants) {
                    return "Valitsemasi ohjelma on täynnä (joku ehti ilmoittautua tässä välissä).";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Tietokantavirhe.";
        }


        try (Connection con = DataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO participant_registration (participant_id, slot, program_id) VALUES (?,?,?)");
        ) {
            ps.setString(1, guid);
            ps.setInt(2, slot);
            ps.setInt(3, programId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Tietokantavirhe.";
        }
        return null;
    }

}