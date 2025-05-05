import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USERS_FILE = "users.txt";
    private static final File COLLECTION_DIR = new File("collections");

    private static Map<String, String> loadUsers() {
        Map<String, String> users = new HashMap<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading users file: " + e.getMessage());
        }

        return users;
    }

    public static String promptLogin() {
        Map<String, String> users = loadUsers();

        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No users found. You must create a new account.");
            return createAccount(users);
        }

        String[] options = {"Login", "Create Account", "Exit"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(null, "Welcome!", "Trading Card App",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (choice == 0) {
                return login(users);
            } else if (choice == 1) {
                return createAccount(users);
            } else {
                return null;
            }
        }
    }

    private static String createAccount(Map<String, String> users) {
        JOptionPane.showMessageDialog(null, "Usernames and Passwords are case-sensitive.", "Case Sensitivity Notice", JOptionPane.INFORMATION_MESSAGE);
        String username = JOptionPane.showInputDialog("Choose a username:");
        if (username == null) return null;
        username = username.trim();

        if (users.containsKey(username)) {
            JOptionPane.showMessageDialog(null, "Username already exists.");
            return null;
        }

        String password = JOptionPane.showInputDialog("Choose a password:");
        if (password == null )return null;
        password = password.trim();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            writer.write(username + ":" + password);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving new user.");
        }

        JOptionPane.showMessageDialog(null, "Account created successfully!");
        return username;
    }

    private static String login(Map<String, String> users) {
        String username = JOptionPane.showInputDialog("Enter username:");
        if (username == null || username.trim().isEmpty()) return null;

        username = username.trim();
        String password = JOptionPane.showInputDialog("Enter password:");
        if (password == null || password.trim().isEmpty()) return null;
        password = password.trim();

        if (users.containsKey(username) && users.get(username).equals(password)) {
            return username;
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or password.");
            return null;
        }
    }

    public static ArrayList<TradingCard> loadCollection(String username) {
        ArrayList<TradingCard> collection = new ArrayList<>();
        File file = new File(COLLECTION_DIR, username + ".txt");

        if (!file.exists()) return collection;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("::", -1);
                if (parts.length == 4) {
                    collection.add(new TradingCard(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading collection: " + e.getMessage());
        }

        return collection;
    }

    public static void saveCollection(String username, ArrayList<TradingCard> collection) {
        if (!COLLECTION_DIR.exists()) COLLECTION_DIR.mkdirs();
        File file = new File(COLLECTION_DIR, username + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (TradingCard card : collection) {
                writer.write(card.getImagePath() + "::" + card.getName() + "::" + card.getYear() + "::" + card.getType());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving collection: " + e.getMessage());
        }
    }
}
