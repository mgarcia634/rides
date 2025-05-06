package gui;

import businessLogic.BLFacade;
import domain.Ride;
import domain.Reservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewReservationsGUI extends JFrame {
    private JComboBox<String> rideSelection;
    private JTextArea reservationList;
    private JButton showReservationsButton;
    private BLFacade facade;
    private String driverEmail; // Email del conductor

    public ViewReservationsGUI(String driverEmail) {
        this.driverEmail = driverEmail;
        setTitle("Ver Solicitudes Pendientes");
        setSize(500, 300);
        setLayout(new BorderLayout());

        facade = MainGUI.getBusinessLogic();

        // ComboBox para seleccionar un viaje
        rideSelection = new JComboBox<>();
        loadDriverRides();

        // Área de texto para mostrar las reservas
        reservationList = new JTextArea();
        reservationList.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reservationList);

        // Botón para ver las reservas del viaje seleccionado
        showReservationsButton = new JButton("Mostrar Solicitudes");
        showReservationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReservations();
            }
        });

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Selecciona un viaje:"));
        topPanel.add(rideSelection);
        topPanel.add(showReservationsButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Cargar los viajes del conductor en el ComboBox
    private void loadDriverRides() {
        List<Ride> rides = facade.getReservationsForDriver(driverEmail);
        for (Ride ride : rides) {
            rideSelection.addItem(ride.getRideNumber() + ": " + ride.getFrom() + " → " + ride.getTo() + " (" + ride.getDate() + ")");
        }
    }

    // Mostrar solicitudes pendientes del viaje seleccionado
    private void showReservations() {
        reservationList.setText("");
        String selectedRide = (String) rideSelection.getSelectedItem();

        if (selectedRide != null) {
            int rideId = Integer.parseInt(selectedRide.split(":")[0]);
            List<Reservation> reservations = facade.getPendingReservationsForRide(rideId);

            if (reservations.isEmpty()) {
                reservationList.setText("No hay solicitudes pendientes para este viaje.");
            } else {
                for (Reservation r : reservations) {
                    reservationList.append(r.getPassenger().getName() + " (" + r.getPassenger().getEmail() + ")\n");
                }
            }
        }
    }
}