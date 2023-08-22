package fi.partio.pajautin.rest;

import fi.partio.pajautin.dao.DataSource;
import fi.partio.pajautin.dao.PrintoutDao;
import fi.partio.pajautin.dao.SheetGrabber;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/api/generateprintouts")
public class PrintoutApi {


    @GET
    @Produces("text/plain")
    public String getIt() {
        try {
            PrintoutDao.generaterPrintouts();
            return "Printouts generated.";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}

