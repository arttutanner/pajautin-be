package fi.partio.pajautin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramDao {

    public static Map<Integer, List<Boolean>> getProgramActiveStatus() {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, act1,act2,act3 FROM program");
             ResultSet rs = ps.executeQuery()) {
            {
                HashMap<Integer, List<Boolean>> programActiveStatus = new HashMap<>();
                while (rs.next()) {
                    programActiveStatus.put(rs.getInt(1), List.of(rs.getBoolean(2), rs.getBoolean(3), rs.getBoolean(4)));
                }
                return programActiveStatus;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public static Map<Integer, List<Integer>> getParticipantCount() {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "select re.program_id, re.slot,  count(re.id) " +
                             "FROM  participant_registration as re " +
                             "group by re.program_id, re.slot");
             ResultSet rs = ps.executeQuery()) {
            {
                HashMap<Integer, List<Integer>> participantCount = new HashMap<>();
                while (rs.next()) {
                    if (participantCount.containsKey(rs.getInt(1))) {
                        participantCount.get(rs.getInt(1)).set(rs.getInt(2)-1, rs.getInt(3));
                    } else {
                        participantCount.put(rs.getInt(1), new ArrayList<>(List.of(0, 0, 0)));
                        participantCount.get(rs.getInt(1)).set(rs.getInt(2)-1, rs.getInt(3));
                    }
                }
                return participantCount;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
