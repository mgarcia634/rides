package gui;

import businessLogic.BLFacade;
import exceptions.UserAlreadyExistsException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterGUI extends JFrame {
    private JTextField emailField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JLabel messageLabel;

    public RegisterGUI() {
        setTitle("Register");
        setSize(300, 250);
        setLayout(new GridLayout(5, 1));

        emailField = new JTextField();
        nameField = new JTextField();
        passwordField = new JPasswordField();
        registerButton = new JButton("Register");
        messageLabel = new JLabel();

        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(registerButton);
        add(messageLabel);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String name = nameField.getText();
                String password = new String(passwordField.getPassword());
                BLFacade facade = MainGUI.getBusinessLogic();
                try {
                    facade.register(email, name, password);
                    messageLabel.setText("Registration successful!");
                } catch (UserAlreadyExistsException ex) {
                    messageLabel.setText("User already exists.");
                }
            }
        });
    }

    public static void main(String[] args) {
        RegisterGUI registerGUI = new RegisterGUI();
        registerGUI.setVisible(true);
    }
}

