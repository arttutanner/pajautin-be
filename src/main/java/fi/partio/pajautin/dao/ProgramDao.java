package fi.partio.pajautin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

}
