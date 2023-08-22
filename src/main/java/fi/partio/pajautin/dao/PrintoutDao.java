package fi.partio.pajautin.dao;

import javax.xml.crypto.Data;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrintoutDao {

    public static void generaterPrintouts() throws IOException, SQLException {

        String baseDir = DataSource.getProperties().getProperty("printout.directory");
        String dateString = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace(":","-");

        generateStatisticsPrintout(baseDir+dateString+"stats.html");
        generateProgramPrintout(baseDir+dateString+"program_lists.html");
        generateParticipantPrintout(baseDir+dateString+"participant_list.html");


    }

    private static void generateParticipantPrintout(String filename) {
    }

    private static void generateProgramPrintout(String filename) throws FileNotFoundException {
        PrintStream out = new PrintStream(new FileOutputStream(filename));
        out.println(getHeader());

        try (Connection conn = DataSource.getConnection(); ResultSet rs = DataSource.getConnection().createStatement().executeQuery("SELECT * FROM program")) {
            while (rs.next()) {


                for (int slot = 1; slot <= 3; slot++) {
                    if (rs.getInt("act" + slot) == 0) continue;
                    out.println("<h1>("+rs.getString("id") +") "+ rs.getString("name") + "</h1>");
                    out.println("<h3>" + rs.getString("author") + "</h3>");
                    out.println("<h3>Luotu " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "</h3>");
                    out.println("<h3>Aikaväli " + slot + "</h3>");
                    printProgramList(rs.getInt("id"), slot, rs.getInt("maxSize"), out, conn);
                    out.println("<div style=\"page-break-after: always;\"></div>");
                }

            }

        }
        catch (Exception e) {
            e.printStackTrace();
            out.println(e.getMessage());
        }
    }

    private static void printProgramList(int programId, int slot, int rowCount, PrintStream out, Connection conn) throws SQLException {

        out.println(getPrintoutAsHTML(
                "SELECT p.id, p.first_name as etunimi, p.last_name as sukunimi, p.email, pr.slot, pr.registration_time " +
                        "FROM participant_registration as pr JOIN participants as p on (pr.participant_id = p.id) " +
                        "WHERE slot="+slot+" AND program_id = "+programId,conn,programId<300 ? rowCount : 1));

    }

    private static void generateStatisticsPrintout(String filename) throws FileNotFoundException, SQLException {
        PrintStream out = new PrintStream(new FileOutputStream(filename));
        out.println(getHeader());
        out.println("<h1>Tilastot</h1>");
        out.println("<h3>Luotu "+LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+"</h3>");
        try (Connection conn = DataSource.getConnection()) {
            out.println(getPrintoutAsHTML(STAT_QUERY,conn));
        }
        out.println(getFooter());
        out.close();
    }

    public static String getPrintoutAsHTML(String sql, Connection conn) throws SQLException {
        return getPrintoutAsHTML(sql,conn,-1);
    }

    public static String getPrintoutAsHTML(String sql, Connection conn, int totalRows) throws SQLException {
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            String s = getPrintoutAsHTML(rs,totalRows);
            rs.close();
            return s;
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }


    /**
     * Returns a HTML table of the printout
     * @param rs ResultSet of the query
     * @return
     */
    public static String getPrintoutAsHTML(ResultSet rs, int totalRows) throws SQLException {

        String html="";

        // header
        html+="<table>\n";
        html+="<tr>";
        if (totalRows>0) {
            html+="<th>#</th>";
        }
        for (int i=0; i<rs.getMetaData().getColumnCount(); i++) {
            html+="<th>"+rs.getMetaData().getColumnName(i+1)+"</th>";
        }
        html+="</tr>\n";

        int rowCount=0;

        while (rs.next()) {
            rowCount++;

            html+="<tr>";
            if (totalRows>0) {
                html+="<td>"+rowCount+"</td>";
            }
            for (int i=0; i<rs.getMetaData().getColumnCount(); i++) {


                html+="<td>"+rs.getString(i+1)+"</td>";
            }
            html+="</tr>\n";
        }

        // empty rows for program list
        for (int i=rowCount+1; i<=totalRows; i++) {
            html+="<tr>";
            html+="<td>"+i+"</td>";
            for (int j=0; j<rs.getMetaData().getColumnCount(); j++) {
                html+="<td>&nbsp;</td>";
            }
            html+="</tr>\n";
        }

        html+="</table>\n";



        return html;
    }

    public static String getHeader() {
        return "<html><head><style>table, th, td {border: 1px solid black;}</style></head><body>\n";
    }

    public static String getFooter() {
        return "</body></html>";
    }


    public static String STAT_QUERY=

            "SELECT \n" +
                    "q.id,q.name,q.author,q.minimum,q.maximum,\n" +
                    "\n" +
                    "IF(q.act1=0,\n" +
                    " IF(q.count_slot_1=0,\"-\",CONCAT(\"ER:\",q.count_slot_1)),\n" +
                    " q.count_slot_1) AS c_slot_1,\n" +
                    " \n" +
                    "IF(q.act2=0,\n" +
                    " IF(q.count_slot_2=0,\"-\",CONCAT(\"ER:\",q.count_slot_2)),\n" +
                    " q.count_slot_2) AS c_slot_1,\n" +
                    "\n" +
                    "IF(q.act3=0,\n" +
                    " IF(q.count_slot_3=0,\"-\",CONCAT(\"ER:\",q.count_slot_3)),\n" +
                    " q.count_slot_3) AS c_slot_3,\n" +
                    " \n" +
                    " \n" +
                    "CONCAT_WS(\" \",\n" +
                    "IF (q.act1=1 AND q.count_slot_1<q.minimum,\"Slot1\",\"\"),\n" +
                    "IF (q.act2=1 AND q.count_slot_2<q.minimum,\"Slot2\",\"\"),\n" +
                    "IF (q.act3=1 AND q.count_slot_3<q.minimum,\"Slot3\",\"\")\n" +
                    ") AS bellow_minimum\n" +
                    "\n" +
                    "FROM\n" +
                    "\n" +
                    "(\n" +
                    "SELECT\n" +
                    "p.id,\n" +
                    "p.name,\n" +
                    "p.author,\n" +
                    "p.minSize AS minimum,\n" +
                    "p.maxSize AS maximum,\n" +
                    "(SELECT COUNT(*) FROM participant_registration WHERE program_id = p.id AND slot=1) AS count_slot_1,\n" +
                    "(SELECT COUNT(*) FROM participant_registration WHERE program_id = p.id AND slot=2) AS count_slot_2,\n" +
                    "(SELECT COUNT(*) FROM participant_registration WHERE program_id = p.id AND slot=3) AS count_slot_3,\n" +
                    "p.act1,\n" +
                    "p.act2,\n" +
                    "p.act3\n" +
                    "\n" +
                    "FROM program AS p\n" +
                    "ORDER BY p.id\n" +
                    ") AS q\n";


}
