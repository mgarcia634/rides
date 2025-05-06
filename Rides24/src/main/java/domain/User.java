package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
public class User implements Serializable {
    @Id
    private String email;
    private String name;
    private String password;
    private float walletBalance = 0;

    public float getWalletBalance() {
        return walletBalance;
    }

    public void addToWallet(float amount) {
        this.walletBalance += amount;
    }

    public boolean deductFromWallet(float amount) {
        if (walletBalance >= amount) {
            walletBalance -= amount;
            return true;
        }
        return false;
    }


    // EXISTENTE: se mantiene
    @ManyToMany
    private List<Ride> reservedRides;

    // NUEVO: solicitudes de reserva realizadas por este usuario
    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservationRequests = new ArrayList<>();

    // NUEVO: calificaciones recibidas por este usuario
    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rating> receivedRatings = new ArrayList<>();

    // OPCIONAL: calificaciones que este usuario ha dado a otros
    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rating> givenRatings = new ArrayList<>();

    public User() {
        this.reservedRides = new ArrayList<>();
    }

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.reservedRides = new ArrayList<>();
    }

    // EXISTENTE
    public List<Ride> getReservedRides() {
        return reservedRides;
    }

    public void addReservation(Ride ride) {
        if (!this.reservedRides.contains(ride)) {
            this.reservedRides.add(ride);
        }
    }

    // NUEVO: acceder a las solicitudes de reserva realizadas
    public List<Reservation> getReservationRequests() {
        return reservationRequests;
    }

    public void addReservationRequest(Reservation reservation) {
        this.reservationRequests.add(reservation);
    }

    // MÉTODO OPCIONAL: cancelar solicitud pendiente
    public void cancelReservationRequest(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.PENDIENTE) {
            this.reservationRequests.remove(reservation);
        }
    }

    public List<Rating> getReceivedRatings() {
        return receivedRatings;
    }

    public void addReceivedRating(Rating rating) {
        this.receivedRatings.add(rating);
    }

    public List<Rating> getGivenRatings() {
        return givenRatings;
    }

    public void addGivenRating(Rating rating) {
        this.givenRatings.add(rating);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
