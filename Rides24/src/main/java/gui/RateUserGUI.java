 package gui;

import businessLogic.BLFacade;
import domain.Driver;
import domain.Rating;
import domain.Reservation;
import domain.ReservationStatus;
import domain.Ride;
import domain.User;
import domain.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RateUserGUI extends JFrame {
    private JComboBox<String> rideComboBox;
    private JComboBox<String> userComboBox;
    private JSpinner ratingSpinner;
    private JTextArea commentArea;

    private JCheckBox rateVehicleCheckBox;
    private JSpinner vehicleRatingSpinner;
    private JTextArea vehicleCommentArea;
    private JLabel vehicleInfoLabel;

    private JButton submitButton;
    private JLabel statusLabel;

    private BLFacade facade;
    private User currentUser;
    private List<Reservation> allAcceptedReservations;

    public RateUserGUI(User user) {
        this.currentUser = user;
        this.facade = MainGUI.getBusinessLogic();

        setTitle("Calificar Usuario y Vehículo");
        setSize(600, 650);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(13, 1));

        rideComboBox = new JComboBox<>();
        userComboBox = new JComboBox<>();

        ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        commentArea = new JTextArea(3, 20);

        rateVehicleCheckBox = new JCheckBox("¿También quieres calificar el vehículo?");
        vehicleRatingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        vehicleCommentArea = new JTextArea(3, 20);
        vehicleInfoLabel = new JLabel("Vehículo: ---");

        submitButton = new JButton("Enviar Calificación");
        statusLabel = new JLabel();

        formPanel.add(new JLabel("Selecciona un viaje completado:"));
        formPanel.add(rideComboBox);
        formPanel.add(new JLabel("Selecciona usuario a calificar:"));
        formPanel.add(userComboBox);
        formPanel.add(new JLabel("Puntuación (1-5):"));
        formPanel.add(ratingSpinner);
        formPanel.add(new JLabel("Comentario (opcional):"));
        formPanel.add(new JScrollPane(commentArea));
        formPanel.add(rateVehicleCheckBox);
        formPanel.add(vehicleInfoLabel);
        formPanel.add(new JLabel("Puntuación del vehículo (1-5):"));
        formPanel.add(vehicleRatingSpinner);
        formPanel.add(new JLabel("Comentario sobre el vehículo:"));
        formPanel.add(new JScrollPane(vehicleCommentArea));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(submitButton, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadRelevantReservations();

        rideComboBox.addActionListener(e -> loadOtherUsers());
        rateVehicleCheckBox.addActionListener(e -> toggleVehicleRatingFields(rateVehicleCheckBox.isSelected()));

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitRating();
            }
        });

        toggleVehicleRatingFields(false);
    }

    private void toggleVehicleRatingFields(boolean visible) {
        vehicleRatingSpinner.setEnabled(visible);
        vehicleCommentArea.setEnabled(visible);
    }

    private void loadRelevantReservations() {
        allAcceptedReservations = facade.getAllAcceptedReservationsForUser(currentUser.getEmail());
        for (Reservation res : allAcceptedReservations) {
            Ride ride = res.getRide();
            boolean isPassenger = res.getPassenger().getEmail().equals(currentUser.getEmail());
            boolean isDriver = ride.getDriver().getEmail().equals(currentUser.getEmail());
            if ((isPassenger || isDriver) && ride.getDate().before(utils.FakeClock.now())) {
                String label = ride.getRideNumber() + ": " + ride.getFrom() + " → " + ride.getTo();
                if (((DefaultComboBoxModel<String>) rideComboBox.getModel()).getIndexOf(label) == -1) {
                    rideComboBox.addItem(label);
                }
            }
        }
    }

    private void loadOtherUsers() {
        userComboBox.removeAllItems();
        vehicleInfoLabel.setText("Vehículo: ---");
        String selected = (String) rideComboBox.getSelectedItem();
        if (selected == null) return;

        int rideId = Integer.parseInt(selected.split(":")[0]);

        for (Reservation res : allAcceptedReservations) {
            Ride ride = res.getRide();
            if (ride.getRideNumber() == rideId) {
                boolean isPassenger = res.getPassenger().getEmail().equals(currentUser.getEmail());
                boolean isDriver = ride.getDriver().getEmail().equals(currentUser.getEmail());

                if (isPassenger) {
                    User other = new User(ride.getDriver().getEmail(), ride.getDriver().getName(), "");
                    userComboBox.addItem(other.getEmail());
                    rateVehicleCheckBox.setEnabled(true);
                    toggleVehicleRatingFields(rateVehicleCheckBox.isSelected());

                    Driver fullDriver = facade.getDriverByEmail(ride.getDriver().getEmail());
                    Vehicle vehicle = fullDriver.getVehicle();

                    if (vehicle != null) {
                        vehicleInfoLabel.setText("Vehículo: " + vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() + ")");
                        if (vehicle.getImage() != null) {
                            showImagePopup(vehicle.getImage());
                        } else {
                            JOptionPane.showMessageDialog(this, "El vehículo no tiene imagen disponible.", "Imagen", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        vehicleInfoLabel.setText("Vehículo: no registrado");
                        JOptionPane.showMessageDialog(this, "El conductor no ha registrado un vehículo.", "Imagen", JOptionPane.INFORMATION_MESSAGE);
                    }

                } else if (isDriver) {
                    User passenger = res.getPassenger();
                    if (!passenger.getEmail().equals(currentUser.getEmail())) {
                        userComboBox.addItem(passenger.getEmail());
                    }
                    rateVehicleCheckBox.setSelected(false);
                    rateVehicleCheckBox.setEnabled(false);
                    toggleVehicleRatingFields(false);
                    vehicleInfoLabel.setText("Vehículo: no disponible para calificación por el conductor");
                }
            }
        }
    }

    private void showImagePopup(byte[] imageData) {
        ImageIcon icon = new ImageIcon(imageData);
        Image scaled = icon.getImage().getScaledInstance(500, 335, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaled));
        JOptionPane.showMessageDialog(this, imageLabel, "Imagen del Vehículo", JOptionPane.PLAIN_MESSAGE);
    }

    private void submitRating() {
        String rideText = (String) rideComboBox.getSelectedItem();
        String toUserEmail = (String) userComboBox.getSelectedItem();
        int score = (int) ratingSpinner.getValue();
        String comment = commentArea.getText();

        if (rideText == null || toUserEmail == null) {
            statusLabel.setText("Selecciona viaje y usuario");
            return;
        }

        int rideId = Integer.parseInt(rideText.split(":")[0]);

        if (facade.hasUserRatedRide(currentUser.getEmail(), toUserEmail, rideId)) {
            statusLabel.setText("Ya has calificado a este usuario en ese viaje.");
            return;
        }

        Ride selectedRide = null;
        for (Reservation res : allAcceptedReservations) {
            if (res.getRide().getRideNumber() == rideId) {
                selectedRide = res.getRide();
                break;
            }
        }

        if (selectedRide != null) {
            User toUser = new User();
            toUser.setEmail(toUserEmail);
            Rating userRating = new Rating(selectedRide, currentUser, toUser, score, comment);
            boolean ok = facade.submitRating(userRating);

            if (rateVehicleCheckBox.isSelected()
                && selectedRide.getDriver().getVehicle() != null
                && !currentUser.getEmail().equals(selectedRide.getDriver().getEmail())) {
                int vScore = (int) vehicleRatingSpinner.getValue();
                String vComment = vehicleCommentArea.getText();
                Vehicle vehicle = selectedRide.getDriver().getVehicle();

                Rating vehicleRating = new Rating(selectedRide, currentUser, vehicle, vScore, vComment);
                facade.submitRating(vehicleRating);
            }

            if (ok) {
                statusLabel.setText("¡Calificación enviada!");
            } else {
                statusLabel.setText("Error al enviar calificación.");
            }
        }
    }
}
