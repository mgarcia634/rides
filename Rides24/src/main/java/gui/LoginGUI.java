package gui;

import businessLogic.BLFacade;
import domain.User;
import exceptions.InvalidLoginException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    public LoginGUI() {
        setTitle("Login");
        setSize(300, 200);
        setLayout(new GridLayout(4, 1));

        emailField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        messageLabel = new JLabel();

        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(loginButton);
        add(messageLabel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                BLFacade facade = MainGUI.getBusinessLogic();
                try {
                    User user = facade.login(email, password);
                    
                    // Guardamos el usuario autenticado en MainGUI
                    MainGUI.setLoggedInUser(user);

                    messageLabel.setText("Welcome " + user.getName() + "!");
                    
                    // Cerrar la ventana de login después de iniciar sesión correctamente
                    dispose();
                } catch (InvalidLoginException ex) {
                    messageLabel.setText("Invalid email or password");
                }
            }
        });
    }
    public static void main(String[] args) {
        LoginGUI login = new LoginGUI();
        login.setVisible(true);
    }
}