
package fi.partio.pajautin.rest;

import fi.partio.pajautin.dao.ParticipantDao;
import fi.partio.pajautin.pojos.LoginStatus;
import fi.partio.pajautin.pojos.Participant;
import fi.partio.pajautin.pojos.SaveStatus;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;

import jakarta.ws.rs.core.MediaType;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.Session;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


// The Java class will be hosted at the URI path "/myresource"
@Path("/api")
public class ClientApi {

    @Inject Request request;

    @GET
    @Produces("text/plain")
    public String getIt() {
        return "pajautin-be is running.";
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    public LoginStatus register(String id) {

        System.out.println("Login:"+id);

        LoginStatus loginStatus = new LoginStatus();
        Participant participant = ParticipantDao.getParticipant(id);
        if (participant != null) {
            setGUID(participant.getId());
            loginStatus.setStatus("ok");
            loginStatus.setParticipant(participant);
        }
        else {
            loginStatus.setStatus("not_found");
        }
        return loginStatus;

    }



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/preferences")
    public List<Integer> getPreferredWorkshopIdList() throws SQLException {
        System.out.println("Save prefs:"+getGUID());
        System.out.println(request.getSession().getIdInternal());
        System.out.println("guid"+request.getSession().getAttribute("guid"));
        return ParticipantDao.loadPreferences(getGUID());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/preferences")
    public SaveStatus savePreferences(List<Integer> preferences) throws SQLException {
        if (ParticipantDao.savePreferences(getGUID(),preferences))
            return new SaveStatus("ok");
        else
            return new SaveStatus("error");
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/presence")
    public SaveStatus savePresence(List<Boolean> presence) throws SQLException {
        if (ParticipantDao.savePresence(getGUID(),presence))
            return new SaveStatus("ok");
        else
            return new SaveStatus("error");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/presence")
    public List<Boolean> getPresence() throws SQLException {
        return ParticipantDao.loadPresence(getGUID());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/logout")
    public LoginStatus logout() {
        Session session = request.getSession();
        session.setValid(false);
        LoginStatus loginStatus = new LoginStatus();
        loginStatus.setStatus("logged_out");
        return loginStatus;
    }

    private String getGUID() {
        Session session = request.getSession();
        return (String)session.getAttribute("guid");
    }

    private void setGUID(String guid) {
        Session session = request.getSession();
        session.setAttribute("guid",guid);
    }

}
