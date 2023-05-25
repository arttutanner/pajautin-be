package fi.partio.pajautin.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Participant {

    private String firstName;
    private String lastName;
    @JsonIgnore
    private String id;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
