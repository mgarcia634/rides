package domain;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Ride implements Serializable {
    @XmlID
    @Id 
    @XmlJavaTypeAdapter(IntegerAdapter.class)
    @GeneratedValue
    private Integer rideNumber;

    private String from;
    private String to;
    private int nPlaces;
    private Date date;
    private float price;

    @ManyToOne
    private Driver driver;

    // NUEVO: lista de solicitudes de reserva
    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservationRequests = new ArrayList<>();

    // ORIGINAL: se mantiene, pero no se persiste (opcional, para compatibilidad)
    @Transient
    private List<User> reservations = new ArrayList<>();

    public Ride() {
        super();
    }

    public Ride(Integer rideNumber, String from, String to, Date date, int nPlaces, float price, Driver driver) {
        this.rideNumber = rideNumber;
        this.from = from;
        this.to = to;
        this.nPlaces = nPlaces;
        this.date = date;
        this.price = price;
        this.driver = driver;
    }

    public Ride(String from, String to, Date date, int nPlaces, float price, Driver driver) {
        this.from = from;
        this.to = to;
        this.nPlaces = nPlaces;
        this.date = date;
        this.price = price;
        this.driver = driver;
    }

    // MÉTODO MODIFICADO: agregar solicitud de reserva
    public void addReservationRequest(Reservation reservation) {
        this.reservationRequests.add(reservation);
    }

    // NUEVO: devolver solicitudes de reserva
    public List<Reservation> getReservationRequests() {
        return reservationRequests;
    }

    // ORIGINAL: mantener para compatibilidad visual
    public void addReservation(User user) {
        reservations.add(user);
    }

    public List<User> getReservations() {
        return reservations;
    }

    public Integer getRideNumber() {
        return rideNumber;
    }

    public void setRideNumber(Integer rideNumber) {
        this.rideNumber = rideNumber;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String origin) {
        this.from = origin;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String destination) {
        this.to = destination;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getnPlaces() {
        return nPlaces;
    }

    public void setnPlaces(int nPlaces) {
        this.nPlaces = nPlaces;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void decreaseAvailableSeats() {
        if (this.nPlaces > 0) {
            this.nPlaces--;
        }
    }

    @Override
    public String toString() {
        return rideNumber + " -> " + from + " → " + to + " | " + date + " | Reservas: " + reservationRequests.size();
    }
}
