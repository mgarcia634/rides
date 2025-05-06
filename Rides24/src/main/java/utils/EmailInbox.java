package utils;

import domain.EmailMessage;

import java.util.*;

public class EmailInbox {
    private static Map<String, List<EmailMessage>> inbox = new HashMap<>();

    public static void storeMessage(EmailMessage message) {
        inbox.computeIfAbsent(message.getTo(), k -> new ArrayList<>()).add(message);
    }

    public static List<EmailMessage> getMessages(String userEmail) {
        return inbox.getOrDefault(userEmail, new ArrayList<>());
    }

    public static void clearMessages(String userEmail) {
        inbox.remove(userEmail);
    }
}
