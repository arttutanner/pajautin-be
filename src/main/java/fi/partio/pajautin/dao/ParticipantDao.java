package fi.partio.pajautin.dao;

import fi.partio.pajautin.pojos.Participant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        if (presence.size()!=3)
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
}