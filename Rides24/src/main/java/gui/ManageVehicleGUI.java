package gui;

import businessLogic.BLFacade;
import domain.Driver;
import domain.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class ManageVehicleGUI extends JFrame {
    private JTextField brandField;
    private JTextField modelField;
    private JTextField licensePlateField;
    private JSpinner seatsSpinner;
    private JButton saveButton;
    private JButton deleteButton;
    private JLabel messageLabel;

    private JButton uploadImageButton; // NUEVO
    private JLabel imagePreviewLabel;  // NUEVO
    private byte[] selectedImageData;  // NUEVO

    private BLFacade facade;
    private Driver driver;

    public ManageVehicleGUI(Driver driver) {
        this.driver = driver;
        this.facade = MainGUI.getBusinessLogic();

        setTitle("Gestionar Vehículo");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(9, 2));

        add(new JLabel("Marca:"));
        brandField = new JTextField();
        add(brandField);

        add(new JLabel("Modelo:"));
        modelField = new JTextField();
        add(modelField);

        add(new JLabel("Matrícula:"));
        licensePlateField = new JTextField();
        add(licensePlateField);

        add(new JLabel("Número de plazas:"));
        seatsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        add(seatsSpinner);

        uploadImageButton = new JButton("Subir Imagen"); // NUEVO
        imagePreviewLabel = new JLabel("Sin imagen", SwingConstants.CENTER); // NUEVO

        add(uploadImageButton);
        add(imagePreviewLabel);

        saveButton = new JButton("Guardar");
        deleteButton = new JButton("Eliminar");
        messageLabel = new JLabel("");

        add(saveButton);
        add(deleteButton);
        add(messageLabel);

        loadVehicleInfo();

        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseImageFile();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveVehicle();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteVehicle();
            }
        });
    }

    private void loadVehicleInfo() {
        Vehicle v = driver.getVehicle();
        if (v != null) {
            brandField.setText(v.getBrand());
            modelField.setText(v.getModel());
            licensePlateField.setText(v.getLicensePlate());
            seatsSpinner.setValue(v.getSeats());

            if (v.getImage() != null) {
                ImageIcon icon = new ImageIcon(v.getImage());
                Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imagePreviewLabel.setIcon(new ImageIcon(scaled));
                imagePreviewLabel.setText("");
            } else {
                imagePreviewLabel.setText("Sin imagen");
            }
        }
    }

    private void chooseImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(selectedFile)) {
            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            	byte[] buffer = new byte[1024];
            	int bytesRead;
            	while ((bytesRead = fis.read(buffer)) != -1) {
            	    baos.write(buffer, 0, bytesRead);
            	}
            	selectedImageData = baos.toByteArray();
            	fis.close();
                ImageIcon icon = new ImageIcon(selectedImageData);
                Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imagePreviewLabel.setIcon(new ImageIcon(scaled));
                imagePreviewLabel.setText("");
            } catch (IOException ex) {
                messageLabel.setText("Error al leer imagen.");
            }
        }
    }

    private void saveVehicle() {
        String brand = brandField.getText();
        String model = modelField.getText();
        String license = licensePlateField.getText();
        int seats = (int) seatsSpinner.getValue();

        Vehicle vehicle = driver.getVehicle();
        if (vehicle == null) {
            vehicle = new Vehicle();
            vehicle.setOwner(driver);
            driver.setVehicle(vehicle);
        }

        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setLicensePlate(license);
        vehicle.setSeats(seats);

        if (selectedImageData != null) {
            vehicle.setImage(selectedImageData); // NUEVO
        }

        facade.updateDriverVehicle(driver);
        driver = facade.getDriverByEmail(driver.getEmail()); // 🔁 Recargar driver actualizado

        messageLabel.setText("Vehículo guardado correctamente.");
    }

    private void deleteVehicle() {
        driver.setVehicle(null);
        facade.updateDriverVehicle(driver);
        driver = facade.getDriverByEmail(driver.getEmail()); // 🔁 Recargar driver actualizado

        brandField.setText("");
        modelField.setText("");
        licensePlateField.setText("");
        seatsSpinner.setValue(1);
        imagePreviewLabel.setIcon(null);
        imagePreviewLabel.setText("Sin imagen");
        selectedImageData = null;

        messageLabel.setText("Vehículo eliminado.");
    }
}
