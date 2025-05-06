package businessLogic;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.jws.WebMethod;
import javax.jws.WebService;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import domain.Ride;
import domain.Driver;
import domain.Rating;
import domain.Reservation;
import domain.ReservationStatus;
import domain.User;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.RideAlreadyExistException;
import exceptions.UserAlreadyExistsException;
import exceptions.InvalidLoginException;

/**
 * It implements the business logic as a web service.
 */
@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation implements BLFacade {
    DataAccess dbManager;

    public BLFacadeImplementation() {
        System.out.println("Creating BLFacadeImplementation instance");
        dbManager = new DataAccess();
    }

    public BLFacadeImplementation(DataAccess da) {
        System.out.println("Creating BLFacadeImplementation instance with DataAccess parameter");
        ConfigXML c = ConfigXML.getInstance();
        dbManager = da;
    }

    @Override
    public User login(String email, String password) throws InvalidLoginException {
        dbManager.open();
        try {
            User user = dbManager.findUserByEmail(email);
            if (user == null || !user.getPassword().equals(password)) {
                throw new InvalidLoginException("Invalid email or password.");
            }
            return user;
        } finally {
            dbManager.close();
        }
    }

    @Override
    public void register(String email, String name, String password) throws UserAlreadyExistsException {
        dbManager.open();
        try {
            if (dbManager.findUserByEmail(email) != null) {
                throw new UserAlreadyExistsException("User with this email already exists.");
            }
            dbManager.registerUser(email, name, password);
        } finally {
            dbManager.close();
        }
    }

    @WebMethod
    public List<String> getDepartCities() {
        dbManager.open();
        List<String> departLocations = dbManager.getDepartCities();
        dbManager.close();
        return departLocations;
    }

    @WebMethod
    public List<String> getDestinationCities(String from) {
        dbManager.open();
        List<String> targetCities = dbManager.getArrivalCities(from);
        dbManager.close();
        return targetCities;
    }

    @WebMethod
    public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
            throws RideMustBeLaterThanTodayException, RideAlreadyExistException {
        dbManager.open();
        Ride ride = dbManager.createRide(from, to, date, nPlaces, price, driverEmail);
        dbManager.close();
        return ride;
    }

    @WebMethod
    public List<Ride> getRides(String from, String to, Date date) {
        dbManager.open();
        List<Ride> rides = dbManager.getRides(from, to, date);
        dbManager.close();
        return rides;
    }

    @WebMethod
    public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
        dbManager.open();
        List<Date> dates = dbManager.getThisMonthDatesWithRides(from, to, date);
        dbManager.close();
        return dates;
    }

    public void close() {
        DataAccess dB4oManager = new DataAccess();
        dB4oManager.close();
    }

    @WebMethod
    public void initializeBD() {
        dbManager.open();
        dbManager.initializeDB();
        dbManager.close();
    }

    @Override
    public List<Ride> getAllRides() {
        dbManager.open();
        List<Ride> rides = dbManager.getAllRides();
        dbManager.close();
        return rides;
    }

    @WebMethod
    public List<Ride> getReservationsForDriver(String driverEmail) {
        dbManager.open();
        List<Ride> rides = dbManager.getReservationsForDriver(driverEmail);
        dbManager.close();
        return rides;
    }

    @Override
    public List<User> getRideReservations(int rideId) {
        dbManager.open();
        List<User> passengers = dbManager.getRideReservations(rideId);
        dbManager.close();
        return passengers;
    }

    @Override
    public boolean addReservationToRide(String userEmail, int rideId) {
        dbManager.open();
        boolean success = dbManager.addReservationToRide(userEmail, rideId);
        dbManager.close();
        return success;
    }

    // NUEVO: Obtener solicitudes de reserva pendientes de un conductor
    @Override
    public List<Reservation> getPendingReservations(String driverEmail) {
        dbManager.open();
        List<Reservation> reservations = dbManager.getPendingReservationRequestsForDriver(driverEmail);
        dbManager.close();
        return reservations;
    }
    
    @Override
    public boolean requestRide(String userEmail, int rideId) {
        dbManager.open();
        boolean result = dbManager.createReservationRequest(userEmail, rideId);
        dbManager.close();
        return result;
    }
    
    @Override
    public List<Reservation> getPendingReservationsForRide(int rideId) {
        dbManager.open();
        List<Reservation> reservations = dbManager.getPendingReservationsForRide(rideId);
        dbManager.close();
        return reservations;
    }
    
    @Override
    public void saveVehicle(Driver driver) {
        dbManager.open();
        dbManager.saveDriverVehicle(driver);
        dbManager.close();
    }
    @Override
    public void updateDriverVehicle(Driver driver) {
        dbManager.open();
        dbManager.saveDriverVehicle(driver);
        dbManager.close();
    }
    
    @Override
    public boolean submitRating(Rating rating) {
        dbManager.open();
        boolean result = dbManager.saveRating(rating);
        dbManager.close();
        return result;
    }

    @Override
    public boolean hasUserRatedRide(String fromUserEmail, String toUserEmail, int rideId) {
        dbManager.open();
        boolean result = dbManager.hasRatingForRide(fromUserEmail, toUserEmail, rideId);
        dbManager.close();
        return result;
    }

    @Override
    public List<Rating> getUserReceivedRatings(String userEmail) {
        dbManager.open();
        List<Rating> result = dbManager.getRatingsReceivedByUser(userEmail);
        dbManager.close();
        return result;
    }

    @Override
    public List<Rating> getVehicleRatings(int vehicleId) {
        dbManager.open();
        List<Rating> result = dbManager.getRatingsForVehicle(vehicleId);
        dbManager.close();
        return result;
    }
    
    @Override
    public List<Reservation> getAllAcceptedReservationsForUser(String userEmail) {
        dbManager.open();
        List<Reservation> list = dbManager.getAcceptedReservationsForUser(userEmail);
        dbManager.close();
        return list;
    }
    
    @Override
    public Driver getDriverByEmail(String email) {
        dbManager.open();
        Driver driver = dbManager.findDriverByEmail(email);
        dbManager.close();
        return driver;
    }



    @Override
    public boolean processRidePayment(String passengerEmail, int rideId) {
        dbManager.open();
        boolean success = dbManager.processRidePayment(passengerEmail, rideId);
        dbManager.close();
        return success;
    }


    @Override
    public void rechargeWallet(String userEmail, float amount) {
        dbManager.open();
        User user = dbManager.findUserByEmail(userEmail);
        if (user != null) {
            user.addToWallet(amount);
            dbManager.saveUser(user);
        }
        dbManager.close();
    }
 
    @Override
    public User getUserByEmail(String email) {
        dbManager.open();
        User user = dbManager.findUserByEmail(email);
        dbManager.close();
        return user;
    }



    // NUEVO: Aceptar o rechazar una solicitud
    @Override
    public boolean respondToReservation(int reservationId, boolean accept) {
        dbManager.open();
        Reservation reservation = dbManager.findReservationById(reservationId);
        boolean result = false;

        if (reservation != null && reservation.getStatus() == ReservationStatus.PENDIENTE) {
            result = dbManager.respondToReservationRequest(reservationId, accept);

            String subject = accept ? "Solicitud aceptada" : "Solicitud rechazada";
            String message;

            if (accept) {
                message = "Tu solicitud para el viaje de " + reservation.getRide().getFrom() + " a " +
                          reservation.getRide().getTo() + " ha sido aceptada.\n" +
                          "Puedes pagar el viaje ahora.\nRideID: " + reservation.getRide().getRideNumber();
            } else {
                message = "Tu solicitud para el viaje de " + reservation.getRide().getFrom() + " a " +
                          reservation.getRide().getTo() + " ha sido rechazada.";
            }

            utils.EmailService.send(reservation.getPassenger().getEmail(), subject, message);
        }

        dbManager.close();
        return result;
    }

}
