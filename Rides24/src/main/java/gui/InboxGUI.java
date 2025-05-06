package gui;

import domain.EmailMessage;
import utils.EmailInbox;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InboxGUI extends JFrame {

    private JTextArea emailArea;
    private String userEmail;

    public InboxGUI(String userEmail) {
        this.userEmail = userEmail;
        setTitle("Buzón de entrada - " + userEmail);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        emailArea = new JTextArea();
        emailArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(emailArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Actualizar");
        refreshButton.addActionListener(e -> loadMessages());
        add(refreshButton, BorderLayout.SOUTH);

        loadMessages();
    }

    private void loadMessages() {
        emailArea.setText(""); // Limpiar

        List<EmailMessage> messages = EmailInbox.getMessages(userEmail);

        if (messages.isEmpty()) {
            emailArea.setText("No tienes correos nuevos.");
        } else {
            for (EmailMessage msg : messages) {
                emailArea.append("Asunto: " + msg.getSubject() + "\n");
                emailArea.append("Fecha: " + msg.getDate() + "\n");
                emailArea.append(msg.getBody() + "\n");
                emailArea.append("------------------------------\n");
            }
        }
    }
}

