package dataAccess;

import java.io.File;
import java.net.NoRouteToHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Driver;
import domain.Rating;
import domain.Reservation;
import domain.ReservationStatus;
import domain.Ride;
import domain.User;
import domain.Vehicle;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;

/**
 * It implements the data access to the objectDb database
 */
public class DataAccess  {
	private  EntityManager  db;
	private  EntityManagerFactory emf;


	ConfigXML c=ConfigXML.getInstance();

     public DataAccess()  {
		if (c.isDatabaseInitialized()) {
			String fileName=c.getDbFilename();

			File fileToDelete= new File(fileName);
			if(fileToDelete.delete()){
				File fileToDeleteTemp= new File(fileName+"$");
				fileToDeleteTemp.delete();

				  System.out.println("File deleted");
				} else {
				  System.out.println("Operation failed");
				}
		}
		open();
		if  (c.isDatabaseInitialized())initializeDB();
		
		System.out.println("DataAccess created => isDatabaseLocal: "+c.isDatabaseLocal()+" isDatabaseInitialized: "+c.isDatabaseInitialized());

		close();

	}
     
    public DataAccess(EntityManager db) {
    	this.db=db;
    }

    public User findUserByEmail(String email) {
        return db.find(User.class, email);
    }

    public void registerUser(String email, String name, String password) {
        db.getTransaction().begin();
        User user = new User(email, name, password);
        db.persist(user);
        db.getTransaction().commit();
    }
	
	/**
	 * This is the data access method that initializes the database with some events and questions.
	 * This method is invoked by the business logic (constructor of BLFacadeImplementation) when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	public void initializeDB(){
		
		db.getTransaction().begin();

		try {

		   Calendar today = Calendar.getInstance();
		   
		   int month=today.get(Calendar.MONTH);
		   int year=today.get(Calendar.YEAR);
		   if (month==12) { month=1; year+=1;}  
	    
		   
		    //Create drivers 
			Driver driver1=new Driver("driver1@gmail.com","Aitor Fernandez");
			Driver driver2=new Driver("driver2@gmail.com","Ane Gaztañaga");
			Driver driver3=new Driver("driver3@gmail.com","Test driver");

			
			//Create rides
			driver1.addRide("Donostia", "Bilbo", UtilDate.newDate(year,month,15), 4, 7);
			driver1.addRide("Donostia", "Gazteiz", UtilDate.newDate(year,month,6), 4, 8);
			driver1.addRide("Bilbo", "Donostia", UtilDate.newDate(year,month,25), 4, 4);

			driver1.addRide("Donostia", "Iruña", UtilDate.newDate(year,month,7), 4, 8);
			
			driver2.addRide("Donostia", "Bilbo", UtilDate.newDate(year,month,15), 3, 3);
			driver2.addRide("Bilbo", "Donostia", UtilDate.newDate(year,month,25), 2, 5);
			driver2.addRide("Eibar", "Gasteiz", UtilDate.newDate(year,month,6), 2, 5);

			driver3.addRide("Bilbo", "Donostia", UtilDate.newDate(year,month,14), 1, 3);

			
						
			db.persist(driver1);
			db.persist(driver2);
			db.persist(driver3);

	
			db.getTransaction().commit();
			System.out.println("Db initialized");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns all the cities where rides depart 
	 * @return collection of cities
	 */
	public List<String> getDepartCities(){
			TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.from FROM Ride r ORDER BY r.from", String.class);
			List<String> cities = query.getResultList();
			return cities;
		
	}
	/**
	 * This method returns all the arrival destinations, from all rides that depart from a given city  
	 * 
	 * @param from the depart location of a ride
	 * @return all the arrival destinations
	 */
	public List<String> getArrivalCities(String from){
		TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.to FROM Ride r WHERE r.from=?1 ORDER BY r.to",String.class);
		query.setParameter(1, from);
		List<String> arrivingCities = query.getResultList(); 
		return arrivingCities;
		
	}
	/**
	 * This method creates a ride for a driver
	 * 
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride
	 * @param date the date of the ride 
	 * @param nPlaces available seats
	 * @param driverEmail to which ride is added
	 * 
	 * @return the created ride, or null, or an exception
	 * @throws RideMustBeLaterThanTodayException if the ride date is before today 
 	 * @throws RideAlreadyExistException if the same ride already exists for the driver
	 */
	public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail) throws  RideAlreadyExistException, RideMustBeLaterThanTodayException {
		System.out.println(">> DataAccess: createRide=> from= "+from+" to= "+to+" driver="+driverEmail+" date "+date);
		try {
			if(new Date().compareTo(date)>0) {
				throw new RideMustBeLaterThanTodayException(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.ErrorRideMustBeLaterThanToday"));
			}
			db.getTransaction().begin();
			
			Driver driver = db.find(Driver.class, driverEmail);
			if (driver.doesRideExists(from, to, date)) {
				db.getTransaction().commit();
				throw new RideAlreadyExistException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.RideAlreadyExist"));
			}
			Ride ride = driver.addRide(from, to, date, nPlaces, price);
			//next instruction can be obviated
			db.persist(driver); 
			db.getTransaction().commit();

			return ride;
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			db.getTransaction().commit();
			return null;
		}
		
		
	}
	
	/**
	 * This method retrieves the rides from two locations on a given date 
	 * 
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride
	 * @param date the date of the ride 
	 * @return collection of rides
	 */
	public List<Ride> getRides(String from, String to, Date date) {
		System.out.println(">> DataAccess: getRides=> from= "+from+" to= "+to+" date "+date);

		List<Ride> res = new ArrayList<>();	
		TypedQuery<Ride> query = db.createQuery("SELECT r FROM Ride r WHERE r.from=?1 AND r.to=?2 AND r.date=?3",Ride.class);   
		query.setParameter(1, from);
		query.setParameter(2, to);
		query.setParameter(3, date);
		List<Ride> rides = query.getResultList();
	 	 for (Ride ride:rides){
		   res.add(ride);
		  }
	 	return res;
	}
	
	public List<Ride> getAllRides() {
	    System.out.println(">> DataAccess: getAllRides");

	    List<Ride> res = new ArrayList<>();
	    TypedQuery<Ride> query = db.createQuery("SELECT r FROM Ride r", Ride.class);
	    List<Ride> rides = query.getResultList();

	    res.addAll(rides);
	    return res;
	}
	

    /**
     * Añade una reserva a un viaje específico.
     * 
     * @param userEmail  Email del usuario que quiere reservar.
     * @param rideId     ID del viaje al que se quiere unir.
     * @return true si la reserva se hizo correctamente, false si hubo un problema.
     */
	public boolean addReservationToRide(String userEmail, int rideId) {
	    System.out.println(">> DataAccess: addReservationToRide => user=" + userEmail + " ride=" + rideId);
	    db.getTransaction().begin();

	    User user = db.find(User.class, userEmail);
	    Ride ride = db.find(Ride.class, rideId);

	    if (user == null || ride == null) {
	        System.out.println("Error: Usuario o viaje no encontrado.");
	        db.getTransaction().rollback();
	        return false;
	    }

	    System.out.println("Antes de la reserva: " + user.getReservedRides().size());

	    // Verificar que el usuario no haya reservado ya el viaje
	    if (!user.getReservedRides().contains(ride)) {
	        user.addReservation(ride);
	        db.merge(user); // Guardar cambios en la base de datos
	        db.getTransaction().commit();

	        System.out.println("Después de la reserva: " + user.getReservedRides().size());
	        return true;
	    } else {
	        System.out.println("El usuario ya ha reservado este viaje.");
	        db.getTransaction().rollback();
	        return false;
	    }
	}


    /**
     * Obtiene todas las reservas de los viajes de un conductor.
     * 
     * @param driverEmail Email del conductor.
     * @return Lista de viajes con sus reservas.
     */
    public List<Ride> getReservationsForDriver(String driverEmail) {
        System.out.println(">> DataAccess: getReservationsForDriver => driver=" + driverEmail);
        TypedQuery<Ride> query = db.createQuery(
            "SELECT r FROM Ride r WHERE r.driver.email = :email", Ride.class
        );
        query.setParameter("email", driverEmail);
        List<Ride> rides = query.getResultList();
        return rides;
    }
    public List<Reservation> getPendingReservationRequestsForDriver(String driverEmail) {
        System.out.println(">> DataAccess: getPendingReservationRequestsForDriver => driver=" + driverEmail);
        TypedQuery<Reservation> query = db.createQuery(
            "SELECT r FROM Reservation r WHERE r.ride.driver.email = :driverEmail AND r.status = :status",
            Reservation.class
        );
        query.setParameter("driverEmail", driverEmail);
        query.setParameter("status", ReservationStatus.PENDIENTE);
        return query.getResultList();
    }
    
    public boolean respondToReservationRequest(int reservationId, boolean accept) {
        System.out.println(">> DataAccess: respondToReservationRequest => id=" + reservationId + " accept=" + accept);
        db.getTransaction().begin();

        Reservation reservation = db.find(Reservation.class, reservationId);

        if (reservation == null || reservation.getStatus() != ReservationStatus.PENDIENTE) {
            db.getTransaction().rollback();
            return false;
        }

        if (accept) {
            reservation.setStatus(ReservationStatus.ACEPTADA);
            reservation.getRide().decreaseAvailableSeats();
        } else {
            reservation.setStatus(ReservationStatus.RECHAZADA);
        }

        db.merge(reservation);
        db.getTransaction().commit();
        return true;
    }


    public List<User> getRideReservations(int rideId) {
        System.out.println(">> DataAccess: getRideReservations => rideId=" + rideId);
        
        TypedQuery<User> query = db.createQuery(
            "SELECT u FROM User u JOIN u.reservedRides r WHERE r.rideNumber = :rideId", User.class);
        query.setParameter("rideId", rideId);
        
        List<User> users = query.getResultList();
        System.out.println("Usuarios que han reservado el viaje " + rideId + ": " + users.size());

        for (User u : users) {
            System.out.println("Usuario reservado: " + u.getEmail());
        }

        return users;
    }


	
	/**
	 * This method retrieves from the database the dates a month for which there are events
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride 
	 * @param date of the month for which days with rides want to be retrieved 
	 * @return collection of rides
	 */
	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
		System.out.println(">> DataAccess: getEventsMonth");
		List<Date> res = new ArrayList<>();	
		
		Date firstDayMonthDate= UtilDate.firstDayMonth(date);
		Date lastDayMonthDate= UtilDate.lastDayMonth(date);
				
		
		TypedQuery<Date> query = db.createQuery("SELECT DISTINCT r.date FROM Ride r WHERE r.from=?1 AND r.to=?2 AND r.date BETWEEN ?3 and ?4",Date.class);   
		
		query.setParameter(1, from);
		query.setParameter(2, to);
		query.setParameter(3, firstDayMonthDate);
		query.setParameter(4, lastDayMonthDate);
		List<Date> dates = query.getResultList();
	 	 for (Date d:dates){
		   res.add(d);
		  }
	 	return res;
	}
	
	public boolean createReservationRequest(String userEmail, int rideId) {
	    System.out.println(">> DataAccess: createReservationRequest => user=" + userEmail + ", ride=" + rideId);
	    db.getTransaction().begin();

	    User user = db.find(User.class, userEmail);
	    Ride ride = db.find(Ride.class, rideId);

	    if (user == null || ride == null) {
	        db.getTransaction().rollback();
	        return false;
	    }

	    Reservation reservation = new Reservation(ride, user);

	    db.persist(reservation);
	    ride.addReservationRequest(reservation);
	    user.addReservationRequest(reservation);

	    db.merge(ride);
	    db.merge(user);
	    db.getTransaction().commit();

	    return true;
	}
	
	public List<Reservation> getPendingReservationsForRide(int rideId) {
	    System.out.println(">> DataAccess: getPendingReservationsForRide => rideId=" + rideId);
	    TypedQuery<Reservation> query = db.createQuery(
	        "SELECT r FROM Reservation r WHERE r.ride.rideNumber = :rideId AND r.status = :status",
	        Reservation.class
	    );
	    query.setParameter("rideId", rideId);
	    query.setParameter("status", ReservationStatus.PENDIENTE);
	    return query.getResultList();
	}
	
	
	

	public void saveDriverVehicle(Driver driver) {
	    System.out.println(">> DataAccess: saveDriverVehicle => " + driver.getEmail());
	    db.getTransaction().begin();
	    db.merge(driver); // guarda el vehículo a través del driver
	    db.getTransaction().commit();
	}
	
	public boolean saveRating(Rating rating) {
	    db.getTransaction().begin();
	    db.persist(rating);
	    db.getTransaction().commit();
	    return true;
	}

	public boolean hasRatingForRide(String fromUserEmail, String toUserEmail, int rideId) {
		TypedQuery<Long> query = db.createQuery(
			    "SELECT COUNT(r) FROM Rating r WHERE r.fromUser.email = :from AND r.toUser.email = :to AND r.ride.rideNumber = :rideId",
			    Long.class
			);

	    query.setParameter("from", fromUserEmail);
	    query.setParameter("to", toUserEmail);
	    query.setParameter("rideId", rideId);

	    Long count = query.getSingleResult();
	    return count != null && count > 0;
	}

	public List<Rating> getRatingsReceivedByUser(String userEmail) {
	    TypedQuery<Rating> query = db.createQuery(
	        "SELECT r FROM Rating r WHERE r.toUser.email = :email", Rating.class
	    );
	    query.setParameter("email", userEmail);
	    return query.getResultList();
	}

	public List<Rating> getRatingsForVehicle(int vehicleId) {
	    TypedQuery<Rating> query = db.createQuery(
	        "SELECT r FROM Rating r WHERE r.vehicle.id = :vehicleId", Rating.class
	    );
	    query.setParameter("vehicleId", vehicleId);
	    return query.getResultList();
	}
	
	public List<Reservation> getAcceptedReservationsForUser(String userEmail) {
	    TypedQuery<Reservation> query = db.createQuery(
	        "SELECT r FROM Reservation r WHERE (r.passenger.email = :email OR r.ride.driver.email = :email) AND r.status = :status",
	        Reservation.class
	    );
	    query.setParameter("email", userEmail);
	    query.setParameter("status", ReservationStatus.ACEPTADA);
	    return query.getResultList();
	}
	
	public Driver findDriverByEmail(String email) {
	    Driver driver = db.find(Driver.class, email);
	    
	    if (driver != null) {
	        Vehicle v = driver.getVehicle();
	        if (v != null) {
	            System.out.println(">>> VEHÍCULO ENCONTRADO para " + email + ": " + v);
	        } else {
	            System.out.println(">>> VEHÍCULO NO ENCONTRADO para " + email);
	        }
	    } else {
	        System.out.println(">>> DRIVER NO ENCONTRADO: " + email);
	    }

	    return driver;
	}


	public Reservation findReservationById(int reservationId) {
	    return db.find(Reservation.class, reservationId);
	}


	public boolean processRidePayment(String userEmail, int rideId) {
	    System.out.println(">> DataAccess: pagarViaje => usuario=" + userEmail + " ride=" + rideId);
	    db.getTransaction().begin();

	    User user = db.find(User.class, userEmail);
	    Ride ride = db.find(Ride.class, rideId);
	    if (user == null || ride == null) {
	        db.getTransaction().rollback();
	        return false;
	    }

	    // Buscar reserva aceptada de este usuario en este viaje
	    Reservation reservation = null;
	    for (Reservation r : ride.getReservationRequests()) {
	        if (r.getPassenger().getEmail().equals(userEmail) && r.getStatus() == ReservationStatus.ACEPTADA) {
	            reservation = r;
	            break;
	        }
	    }

	    if (reservation == null) {
	        db.getTransaction().rollback();
	        return false;
	    }

	    float price = ride.getPrice();

	    // Verificar que el usuario tiene saldo suficiente
	    if (user.getWalletBalance() < price) {
	        db.getTransaction().rollback();
	        return false;
	    }

	    // Restar saldo del pasajero
	    user.deductFromWallet(price);
	    db.merge(user);

	    // Buscar objeto User correspondiente al conductor para ingresarle el dinero
	    User driverAsUser = db.find(User.class, ride.getDriver().getEmail());
	    if (driverAsUser != null) {
	        driverAsUser.addToWallet(price);
	        db.merge(driverAsUser);
	    } else {
	        System.out.println(">> Error: no se encontró el User del conductor con email: " + ride.getDriver().getEmail());
	        db.getTransaction().rollback();
	        return false;
	    }

	    db.getTransaction().commit();
	    return true;
	}



	public void saveUser(User user) {
	    db.getTransaction().begin();
	    db.merge(user);
	    db.getTransaction().commit();
	}



public void open(){
		
		String fileName=c.getDbFilename();
		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:"+fileName);
			db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<>();
			  properties.put("javax.persistence.jdbc.user", c.getUser());
			  properties.put("javax.persistence.jdbc.password", c.getPassword());

			  emf = Persistence.createEntityManagerFactory("objectdb://"+c.getDatabaseNode()+":"+c.getDatabasePort()+"/"+fileName, properties);
			  db = emf.createEntityManager();
    	   }
		System.out.println("DataAccess opened => isDatabaseLocal: "+c.isDatabaseLocal());

		
	}

	public void close(){
		db.close();
		System.out.println("DataAcess closed");
	}
	
}
