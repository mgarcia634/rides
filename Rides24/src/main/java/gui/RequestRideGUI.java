package gui;

import businessLogic.BLFacade;
import domain.Ride;
import domain.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RequestRideGUI extends JFrame {
    private JComboBox<String> rideSelection;
    private JButton showRidesButton;
    private JButton reserveButton;
    private JLabel messageLabel;
    private BLFacade facade;
    private User loggedInUser;
    private List<Ride> availableRides;

    public RequestRideGUI() {
        setTitle("Request a Ride");
        setSize(400, 250);
        setLayout(new GridLayout(5, 1));

        facade = MainGUI.getBusinessLogic();
        loggedInUser = MainGUI.getLoggedInUser();

        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(this, "Debes iniciar sesión para solicitar un viaje.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        rideSelection = new JComboBox<>();
        rideSelection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reserveButton.setEnabled(rideSelection.getSelectedItem() != null);
            }
        });

        showRidesButton = new JButton("Mostrar viajes");
        showRidesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rideSelection.removeAllItems();
                availableRides = facade.getAllRides();
                for (Ride ride : availableRides) {
                    rideSelection.addItem(ride.getFrom() + " → " + ride.getTo() + " (" + ride.getDate() + ")");
                }

                if (availableRides.isEmpty()) {
                    messageLabel.setText("No hay viajes disponibles.");
                    reserveButton.setEnabled(false);
                } else {
                    messageLabel.setText("Seleccione un viaje para continuar.");
                }
            }
        });

        reserveButton = new JButton("Solicitar Reserva");
        reserveButton.setEnabled(false);
        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rideSelection.getSelectedItem() != null) {
                    int selectedIndex = rideSelection.getSelectedIndex();
                    if (selectedIndex >= 0 && selectedIndex < availableRides.size()) {
                        Ride selectedRide = availableRides.get(selectedIndex);
                        boolean success = facade.requestRide(loggedInUser.getEmail(), selectedRide.getRideNumber());

                        if (success) {
                            messageLabel.setText("Solicitud enviada correctamente para: " + selectedRide.getFrom() + " → " + selectedRide.getTo());
                        } else {
                            messageLabel.setText("No se pudo enviar la solicitud.");
                        }
                    } else {
                        messageLabel.setText("Error: Índice de viaje no válido.");
                    }
                }
            }
        });

        messageLabel = new JLabel();

        add(rideSelection);
        add(showRidesButton);
        add(reserveButton);
        add(messageLabel);
    }

    public static void main(String[] args) {
        RequestRideGUI requestRideGUI = new RequestRideGUI();
        requestRideGUI.setVisible(true);
    }
}