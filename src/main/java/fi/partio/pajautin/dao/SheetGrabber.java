package fi.partio.pajautin.dao;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class SheetGrabber {

    public static int grab(String url, String outFile) {

        Map<Integer, List<Boolean>> programActiveStatus = ProgramDao.getProgramActiveStatus();

        try {
            JSONObject json = getJson(new URL(url));
            JSONArray rows = (JSONArray) json.get("values");
            JSONArray header = (JSONArray) rows.get(0);

            JSONArray output = new JSONArray();
            int count =0;
            for (int i = 1; i < rows.length(); i++) {
                JSONArray row = (JSONArray) rows.get(i);
                JSONObject obj = new JSONObject();
                if (row.length()>0 && !row.get(0).toString().equals("")) {
                    for (int j = 0; j < row.length(); j++) {
                        obj.put(header.get(j).toString(), row.get(j));
                    }
                    count ++;
                    output.put(obj);
                }
                try {
                    Integer programId = Integer.parseInt(obj.get("id").toString());
                    if (programActiveStatus != null && programActiveStatus.containsKey(programId)) {
                        List<Boolean> activeStatus = programActiveStatus.get(programId);
                        obj.put("act1", activeStatus.get(0));
                        obj.put("act2", activeStatus.get(1));
                        obj.put("act3", activeStatus.get(2));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println(output.toString(2)); // pretty print
            // write output to file
            IOUtils.write(output.toString(2), new FileOutputStream(outFile));
            return count;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }

    public static JSONObject getJson(URL url) throws IOException {
        String json = IOUtils.toString(url, Charset.forName("UTF-8"));
        return new JSONObject(json);
    }

}
