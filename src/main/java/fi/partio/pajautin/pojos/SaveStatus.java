package fi.partio.pajautin.pojos;

import java.text.SimpleDateFormat;

public class SaveStatus {
    String status;
    String date;

    String errorMessage;

    static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public SaveStatus(String status) {
        this.status = status;
        // format date as finnish locale
        this.date = sdf.format(new java.util.Date());

    }

    public SaveStatus(String status, String errorMessage) {
        this.status = status;
        // format date as finnish locale
        this.date = sdf.format(new java.util.Date());
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
