package domain;

import java.util.Date;

public class EmailMessage {
    private String to;
    private String subject;
    private String body;
    private Date date;

    public EmailMessage(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.date = new Date();
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "[" + date + "] " + subject + " -> " + body;
    }
}
