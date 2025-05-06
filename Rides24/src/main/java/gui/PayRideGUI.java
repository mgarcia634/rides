package gui;

import businessLogic.BLFacade;
import domain.Ride;
import domain.Reservation;
import domain.ReservationStatus;
import domain.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class PayRideGUI extends JFrame {

    private JComboBox<String> rideComboBox;
    private JButton payButton;
    private JLabel messageLabel;

    private BLFacade facade;
    private User currentUser;
    private List<Reservation> acceptedReservations;

    public PayRideGUI(User user) {
        this.currentUser = user;
        this.facade = MainGUI.getBusinessLogic();

        setTitle("Pagar Viaje");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        rideComboBox = new JComboBox<>();
        loadAcceptedReservations();

        payButton = new JButton("Pagar viaje");
        payButton.addActionListener(this::handlePayment);

        messageLabel = new JLabel("", SwingConstants.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Selecciona un viaje aceptado:"), BorderLayout.NORTH);
        centerPanel.add(rideComboBox, BorderLayout.CENTER);
        centerPanel.add(payButton, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);
    }

    private void loadAcceptedReservations() {
        acceptedReservations = facade.getAllAcceptedReservationsForUser(currentUser.getEmail());

        for (Reservation r : acceptedReservations) {
            if (r.getPassenger().getEmail().equals(currentUser.getEmail()) &&
                r.getStatus() == ReservationStatus.ACEPTADA) {

                Ride ride = r.getRide();
                rideComboBox.addItem(ride.getRideNumber() + ": " + ride.getFrom() + " → " + ride.getTo() + " | " + ride.getDate());
            }
        }
    }

    private void handlePayment(ActionEvent e) {
        String selected = (String) rideComboBox.getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Selecciona un viaje primero.");
            return;
        }

        int rideId = Integer.parseInt(selected.split(":")[0]);
        boolean success = facade.processRidePayment(currentUser.getEmail(), rideId);

        if (success) {
        	MainGUI.refreshLoggedInUser();
            messageLabel.setText("Pago realizado correctamente.");
        } else {
            messageLabel.setText("No se pudo realizar el pago (saldo insuficiente o error).");
        }
    }
}