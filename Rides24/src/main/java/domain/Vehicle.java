package domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Vehicle implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    private String brand;
    private String model;
    private String licensePlate;
    private int seats;
    
    @Lob
    private byte[] image;


    @OneToOne(mappedBy = "vehicle")
    private Driver owner;

    public Vehicle() {}

    public Vehicle(String brand, String model, String licensePlate, int seats, Driver owner) {
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.seats = seats;
        this.owner = owner;
    }

    public Integer getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public Driver getOwner() {
        return owner;
    }

    public void setOwner(Driver owner) {
        this.owner = owner;
    }
    
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
    
   


    @Override
    public String toString() {
        return brand + " " + model + " (" + licensePlate + "), " + seats + " plazas";
    }
}
