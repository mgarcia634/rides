package domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Rating implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Ride ride;

    @ManyToOne
    private User fromUser;

    @ManyToOne
    private User toUser;

    @ManyToOne
    private Vehicle vehicle; // solo se usa si se califica un coche

    private int score; // de 1 a 5

    private String comment;

    private Date date;

    public Rating() {}

    public Rating(Ride ride, User fromUser, User toUser, int score, String comment) {
        this.ride = ride;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.score = score;
        this.comment = comment;
        this.date = new Date();
    }

    public Rating(Ride ride, User fromUser, Vehicle vehicle, int score, String comment) {
        this.ride = ride;
        this.fromUser = fromUser;
        this.vehicle = vehicle;
        this.score = score;
        this.comment = comment;
        this.date = new Date();
    }

    public Integer getId() {
        return id;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Puntuación: " + score + ", Comentario: " + comment;
    }
} 
