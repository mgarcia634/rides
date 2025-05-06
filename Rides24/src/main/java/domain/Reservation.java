package domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
public class Reservation implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Ride ride;

    @ManyToOne
    private User passenger;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public Reservation() {
        this.createdAt = new Date();
        this.status = ReservationStatus.PENDIENTE;
    }

    public Reservation(Ride ride, User passenger) {
        this.ride = ride;
        this.passenger = passenger;
        this.createdAt = new Date();
        this.status = ReservationStatus.PENDIENTE;
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

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
