package gui;

import businessLogic.BLFacade;
import domain.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RechargeWalletGUI extends JFrame {
    private User user;
    private JTextField amountField;
    private JLabel statusLabel;
    private BLFacade facade;

    public RechargeWalletGUI(User user) {
        this.user = user;
        this.facade = MainGUI.getBusinessLogic();

        setTitle("Recargar Monedero");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        add(new JLabel("Introduce la cantidad a recargar:"));

        amountField = new JTextField();
        add(amountField);

        JButton rechargeButton = new JButton("Recargar");
        rechargeButton.addActionListener(this::handleRecharge);
        add(rechargeButton);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        add(statusLabel);
    }

    private void handleRecharge(ActionEvent e) {
        try {
            float amount = Float.parseFloat(amountField.getText());
            if (amount <= 0) {
                statusLabel.setText("Introduce una cantidad positiva.");
                return;
            }

            facade.rechargeWallet(user.getEmail(), amount);
            MainGUI.refreshLoggedInUser();
            statusLabel.setText("Recarga exitosa. Nuevo saldo: " + (user.getWalletBalance() + amount) + " €");

        } catch (NumberFormatException ex) {
            statusLabel.setText("Cantidad no válida.");
        }
    }
}