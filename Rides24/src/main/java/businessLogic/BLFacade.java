package businessLogic;

import java.util.Date;
import java.util.List;

import domain.Ride;
import domain.Driver;
import domain.Rating;
import domain.Reservation;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.RideAlreadyExistException;

import javax.jws.WebMethod;
import javax.jws.WebService;

import domain.User;
import exceptions.UserAlreadyExistsException;
import exceptions.InvalidLoginException;

/**
 * Interface that specifies the business logic.
 */
@WebService
public interface BLFacade  {
	  
	@WebMethod public List<String> getDepartCities();

	@WebMethod public List<String> getDestinationCities(String from);

	@WebMethod
	public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
			throws RideMustBeLaterThanTodayException, RideAlreadyExistException;

	@WebMethod public List<Ride> getRides(String from, String to, Date date);
	
	public List<Ride> getAllRides();

	@WebMethod public List<Date> getThisMonthDatesWithRides(String from, String to, Date date);
	
	@WebMethod public void initializeBD();

	User login(String email, String password) throws InvalidLoginException;

	void register(String email, String name, String password) throws UserAlreadyExistsException;

	@WebMethod public List<Ride> getReservationsForDriver(String driverEmail);

	@WebMethod public List<User> getRideReservations(int rideId);

	@WebMethod public boolean addReservationToRide(String userEmail, int rideId);

	// NUEVOS MÉTODOS PARA SOLICITUDES DE RESERVA

	@WebMethod
	public List<Reservation> getPendingReservations(String driverEmail);

	@WebMethod
	public boolean respondToReservation(int reservationId, boolean accept);
	
	@WebMethod
	public boolean requestRide(String userEmail, int rideId);

	@WebMethod
	public List<Reservation> getPendingReservationsForRide(int rideId);

	@WebMethod
	public void saveVehicle(Driver driver);

	@WebMethod
	public void updateDriverVehicle(Driver driver);
	
	@WebMethod
	public boolean submitRating(Rating rating);

	@WebMethod
	public boolean hasUserRatedRide(String fromUserEmail, String toUserEmail, int rideId);

	@WebMethod
	public List<Rating> getUserReceivedRatings(String userEmail);

	@WebMethod
	public List<Rating> getVehicleRatings(int vehicleId);
	
	@WebMethod
	public List<Reservation> getAllAcceptedReservationsForUser(String userEmail);

	@WebMethod
	public Driver getDriverByEmail(String email);
	
	@WebMethod
	boolean processRidePayment(String passengerEmail, int rideId);
	
	@WebMethod
	public void rechargeWallet(String userEmail, float amount);

	@WebMethod
	public User getUserByEmail(String email);




}
