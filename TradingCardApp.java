import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
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

        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setFont(new Font("Arial", Font.BOLD, 14));

        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(33, 150, 243));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setPreferredSize(new Dimension(100,30));

        controlPanel.add(searchField);
        controlPanel.add(searchButton);


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
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit? \nYour collection will be saved regardless.", "Confirm Exit", JOptionPane.YES_NO_OPTION);
           if (confirmation == JOptionPane.YES_OPTION) {
               UserManager.saveCollection(currentUser, cardCollection);

               System.exit(0);
           }
        });

        // Update card display
        updateCardDisplay();

        setVisible(true);
    }

    private void addCard() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a Card Image");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (JPG,PNG,GIF)", "jpg", "png", "gif");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File imageFile = fileChooser.getSelectedFile();
        String name = JOptionPane.showInputDialog(this, "Enter card name:");
        if(name == null || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a card name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String year = JOptionPane.showInputDialog(this, "Enter card year (or leave blank):");
        if(year == null || year.isEmpty())  year = "N/A" ;

        String type = JOptionPane.showInputDialog(this, "Enter card type(or leave blank):");
        if(type == null || type.isEmpty()) type = "N/A";
        //Validating the image and if updateCardDisplay checks to see if it's viable for upload or not
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

        ArrayList<TradingCard> matches = new ArrayList<>();
        for (TradingCard card : cardCollection) {
            if (card.getName().equals(nameToRemove.trim())) {
                matches.add(card);
            }
        }
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No card found with that name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        TradingCard selectedCard;
        if (matches.size() == 1) {
            selectedCard = matches.get(0);
        } else {
            String[] options = new String[matches.size()];
            for (int i = 0; i < matches.size(); i++) {
                TradingCard c = matches.get(i);
                options[i] = String.format("Name: %s | Year: %s | Type: %s", c.getName(), c.getYear(), c.getType());
            }
            String selected = (String) JOptionPane.showInputDialog(this, "Select the card to remove:",
                    "Choose Card", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (selected == null) return; //user cancels the remove process
            selectedCard = matches.get(java.util.Arrays.asList(options).indexOf(selected));
        }
//confirms with user before it removes card
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this card?\n" + selectedCard.getName() + "(" + selectedCard.getYear() + " _ " + selectedCard.getType() + ")",
                "Confirm ", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            cardCollection.remove(selectedCard);
            updateCardDisplay();
            JOptionPane.showMessageDialog(this, "Card Removed.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void updateCardDisplay() {
        cardPanel.removeAll();

        for (TradingCard card : cardCollection) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2));
            panel.setLayout(new BorderLayout());

            // Image Label
            ImageIcon icon;
                    try {
                        icon = new ImageIcon(card.getImagePath());
                        //checks if image did/nt load
                        if (icon.getIconWidth() == -1) { //-1 detects if the image is broken or missing
                            throw new IOException("Invalid Image please try again");
                        }
                    }
                    catch (Exception e) {
                        icon = new ImageIcon(card.getImagePath());
                    }
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
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        }catch(Exception e) {
            System.out.println("Nimbus Look and Feel Not Supported");
        }
        SwingUtilities.invokeLater(() -> {
            String user = UserManager.promptLogin();
            if (user != null) new TradingCardApp(user);
        });
    }
}


