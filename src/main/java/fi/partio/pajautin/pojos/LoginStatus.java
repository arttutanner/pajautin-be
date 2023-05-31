package fi.partio.pajautin.pojos;

import fi.partio.pajautin.dao.ParticipantDao;

public class LoginStatus {

    String status;
    Participant participant;

    public LoginStatus() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
}
