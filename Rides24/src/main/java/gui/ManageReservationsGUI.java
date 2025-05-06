package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import businessLogic.BLFacade;
import domain.Reservation;
import domain.ReservationStatus;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManageReservationsGUI extends JFrame {
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JButton acceptButton;
    private JButton rejectButton;
    private BLFacade facade;
    private String driverEmail;

    public ManageReservationsGUI(String driverEmail) {
        this.driverEmail = driverEmail;
        this.facade = MainGUI.getBusinessLogic();
        initialize();
        loadReservations();
    }

    private void initialize() {
        setTitle("Gestionar Solicitudes de Reserva");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"ID", "Pasajero", "Origen", "Destino", "Fecha"}, 0);
        reservationTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        acceptButton = new JButton("Aceptar");
        rejectButton = new JButton("Rechazar");

        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        add(buttonPanel, BorderLayout.SOUTH);

        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                respondToReservation(true);
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                respondToReservation(false);
            }
        });
    }

    private void loadReservations() {
        tableModel.setRowCount(0); // Clear existing data
        List<Reservation> reservations = facade.getPendingReservations(driverEmail);

        for (Reservation r : reservations) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getPassenger().getEmail(),
                r.getRide().getFrom(),
                r.getRide().getTo(),
                r.getRide().getDate()
            });
        }
    }

    private void respondToReservation(boolean accept) {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una solicitud de la tabla.");
            return;
        }

        Integer reservationId = (Integer) tableModel.getValueAt(selectedRow, 0);
        boolean success = facade.respondToReservation(reservationId, accept);

        if (success) {
            JOptionPane.showMessageDialog(this, accept ? "Reserva aceptada." : "Reserva rechazada.");
            loadReservations();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo procesar la solicitud.");
        }
    }
}
