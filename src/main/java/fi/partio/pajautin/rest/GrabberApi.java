package fi.partio.pajautin.rest;

import fi.partio.pajautin.dao.DataSource;
import fi.partio.pajautin.dao.SheetGrabber;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.IOException;


@Path("/api/grabsheet")
public class GrabberApi {


    @GET
    @Produces("text/plain")
    public String getIt() {
        try {
            return SheetGrabber.grab(DataSource.getProperties().getProperty("sheet.url"), DataSource.getProperties().getProperty("sheet.outfile")) + " rows grabbed.";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
