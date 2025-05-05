import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class TradingCardApp extends JFrame {
    private ArrayList<TradingCard> cardCollection = new ArrayList<>(); //makes an array list that stores the cards within the collection
    private JPanel cardPanel; //used for panel display, each card will have its own panel
    private String currentUser; //displayed in the top of the window - stores/displays username

    public TradingCardApp(String username) {
        this.currentUser = username;
        this.cardCollection = UserManager.loadCollection(username);

        // Main Frame Setup
        setTitle("Trading Card Collection - User: " + username); //displays username on top of window
        setDefaultCloseOperation(EXIT_ON_CLOSE); //exits program when X button or software is closed
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());
        setResizable(true); // makes it so window is resizeable/fullscreen

        // Top panel for controls
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(40, 40, 40));
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));  // Adjust spacing
        controlPanel.setPreferredSize(new Dimension(800, 100));

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
        //allows for live search functionality by updating as search input changes
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }

            private void performSearch() {
                String query = searchField.getText().trim().toLowerCase();
                if (query.isEmpty()) {
                    updateCardDisplay(); // restore full collection
                    return;
                }

                ArrayList<TradingCard> filteredCards = new ArrayList<>();
                for (TradingCard card : cardCollection) {
                    if (card.getName().toLowerCase().contains(query) ||
                            card.getYear().toLowerCase().contains(query) ||
                            card.getType().toLowerCase().contains(query)) {
                        filteredCards.add(card);
                    }
                }

                updateCardDisplay(filteredCards);
            }
        });

        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(33, 150, 243));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setPreferredSize(new Dimension(100,30));

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            if (query.isEmpty()) {
                updateCardDisplay(); // Show all if search is empty
                return;
            }

            ArrayList<TradingCard> filteredCards = new ArrayList<>();
            for (TradingCard card : cardCollection) {
                if (card.getName().toLowerCase().contains(query) ||
                        card.getYear().toLowerCase().contains(query) ||
                        card.getType().toLowerCase().contains(query)) {
                    filteredCards.add(card);
                }
            }

            if (filteredCards.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No Cards matching with search Found");
            }
            updateCardDisplay(filteredCards);
        });
        searchField.addActionListener(e -> searchButton.doClick());


        controlPanel.add(searchField);
        controlPanel.add(searchButton);

       /* JButton clearButton = new JButton("Clear");
        clearButton.setBackground(new Color(158, 158, 158)); //light gray color
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setPreferredSize(new Dimension(100, 30));
*/
        // Add buttons to control panel
        controlPanel.add(addButton);
        controlPanel.add(removeButton);
        controlPanel.add(exitButton);
      //  controlPanel.add(clearButton);
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
       // clearButton.addActionListener(e -> {
         //   searchField.setText("");
           // updateCardDisplay();
       //
        // });
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
    //overloaded method in order to properly search with different parameters, to be more specific filtered cards
    private void updateCardDisplay(ArrayList<TradingCard> cardsToDisplay) {
        cardPanel.removeAll();

        for (TradingCard card : cardsToDisplay) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2));
            panel.setLayout(new BorderLayout());

            // Image Label
            ImageIcon icon;
            try {
                icon = new ImageIcon(card.getImagePath());
                if (icon.getIconWidth() == -1) {
                    throw new IOException("Invalid image");
                }
            } catch (Exception e) {
                icon = new ImageIcon(); // fallback if image is broken
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


