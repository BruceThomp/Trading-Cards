import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class TradingCardApp extends JFrame {
    private ArrayList<TradingCard> cardCollection = new ArrayList<>();
    private JPanel cardPanel;
    private String currentUser;

    public TradingCardApp(String username) {
        this.currentUser = username;
        this.cardCollection = UserManager.loadCollection(username);

        // Main Frame Setup
        setTitle("Trading Card Collection - User: " + username);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());
        setResizable(false);

        // Top panel for controls
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(40, 40, 40));
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));  // Adjust spacing

        JButton addButton = new JButton("Add Card");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setIcon(new ImageIcon("icons/add_icon.png"));
        addButton.setBackground(new Color(0, 150, 136));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(new Dimension(150, 40));  // Set preferred size for the button

        JButton removeButton = new JButton("Remove Card");
        removeButton.setFont(new Font("Arial", Font.BOLD, 14));
        removeButton.setIcon(new ImageIcon("icons/remove_icon.png"));
        removeButton.setBackground(new Color(255, 82, 82));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.setPreferredSize(new Dimension(150, 40));  // Set preferred size for the button

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.setIcon(new ImageIcon("icons/exit_icon.png"));
        exitButton.setBackground(new Color(244, 67, 54));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(150, 40));  // Set preferred size for the button

        // Add buttons to control panel
        controlPanel.add(addButton);
        controlPanel.add(removeButton);
        controlPanel.add(exitButton);
        add(controlPanel, BorderLayout.NORTH);

        // Card display area
        cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(0, 2, 10, 10));
        cardPanel.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(cardPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Button actions
        addButton.addActionListener(e -> addCard());
        removeButton.addActionListener(e -> removeCard());
        exitButton.addActionListener(e -> {
            UserManager.saveCollection(currentUser, cardCollection);
            System.exit(0);
        });

        // Update card display
        updateCardDisplay();

        setVisible(true);
    }

    private void addCard() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a Card Image");
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File imageFile = fileChooser.getSelectedFile();
        String name = JOptionPane.showInputDialog(this, "Enter card name:");
        String year = JOptionPane.showInputDialog(this, "Enter card year:");
        String type = JOptionPane.showInputDialog(this, "Enter card type:");

        if (name != null && year != null && type != null) {
            cardCollection.add(new TradingCard(imageFile.getAbsolutePath(), name, year, type));
            updateCardDisplay();
        }
    }

    private void removeCard() {
        if (cardCollection.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Your collection is empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nameToRemove = JOptionPane.showInputDialog(this, "Enter the name of the card to remove:");
        if (nameToRemove == null || nameToRemove.trim().isEmpty()) return;

        boolean removed = cardCollection.removeIf(card -> card.getName().equalsIgnoreCase(nameToRemove));
        if (!removed) {
            JOptionPane.showMessageDialog(this, "❌ No card found with that name.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        updateCardDisplay();
    }

    private void updateCardDisplay() {
        cardPanel.removeAll();

        for (TradingCard card : cardCollection) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2));
            panel.setLayout(new BorderLayout());

            // Image Label
            ImageIcon icon = new ImageIcon(card.getImagePath());
            Image scaledImg = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));

            // Info Label
            JPanel infoPanel = new JPanel();
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            JLabel nameLabel = new JLabel("Name: " + card.getName());
            JLabel yearLabel = new JLabel("Year: " + card.getYear());
            JLabel typeLabel = new JLabel("Type: " + card.getType());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
            yearLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            infoPanel.add(nameLabel);
            infoPanel.add(yearLabel);
            infoPanel.add(typeLabel);

            panel.add(imgLabel, BorderLayout.CENTER);
            panel.add(infoPanel, BorderLayout.SOUTH);

            cardPanel.add(panel);
        }

        cardPanel.revalidate();
        cardPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String user = UserManager.promptLogin();
            if (user != null) new TradingCardApp(user);
        });
    }
}
