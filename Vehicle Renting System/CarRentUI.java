import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class CarRentUI extends JFrame {

    public CarRentUI() {
        setTitle("Vehicle Renting System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        // Set the background image
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon backgroundIcon = new ImageIcon("C:\\Users\\HP\\Desktop\\car_rent\\blog_173737875_1651756346.jpg");
                    Image backgroundImage = backgroundIcon.getImage();
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    System.err.println("Error loading background image: " + e.getMessage());
                    setBackground(new Color(240, 240, 240));
                }
            }
        });
        getContentPane().setLayout(new BorderLayout());

        // Logo Panel
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        try {
            logoPanel.add(new JLabel(new ImageIcon("C:\\path\\to\\your\\logo.png")));
        } catch (Exception e) {
            System.err.println("Error loading logo image: " + e.getMessage());
        }
        add(logoPanel, BorderLayout.NORTH);

        // Main Button Panel using BoxLayout
        JPanel mainButtonPanel = new JPanel();
        mainButtonPanel.setLayout(new BoxLayout(mainButtonPanel, BoxLayout.Y_AXIS));
        mainButtonPanel.setOpaque(false);
        mainButtonPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Color buttonColor = new Color(0, 123, 255);
        Color buttonTextColor = Color.WHITE;
        Dimension buttonSize = new Dimension(150, 40);

        JButton rentButton = createStyledButton("Rent a Vehicle", buttonFont, buttonColor, buttonTextColor);
        rentButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Make buttons take full width
        rentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rentButton.addActionListener(e -> openVehicleTypeSelectionUI());

        JButton returnButton = createStyledButton("Return a Vehicle", buttonFont, buttonColor, buttonTextColor);
        returnButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        returnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        returnButton.addActionListener(e -> openReturnVehicleUI());

        JButton registerButton = createStyledButton("Register Vehicle", buttonFont, buttonColor, buttonTextColor);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> openVehicleRegistrationUI());

        mainButtonPanel.add(rentButton);
        mainButtonPanel.add(Box.createVerticalStrut(10)); // Add vertical space between buttons
        mainButtonPanel.add(returnButton);
        mainButtonPanel.add(Box.createVerticalStrut(10));
        mainButtonPanel.add(registerButton);

        add(mainButtonPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);

        JButton showCarsButton = createBottomButton("Show Available Cars", e -> showCars());
        JLabel bookIdLabel = new JLabel("Book by ID:");
        bookIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JTextField carIdField = new JTextField(5);
        JButton bookButton = createBottomButton("Book", e -> {
            try {
                int id = Integer.parseInt(carIdField.getText());
                bookCar(id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Car ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bottomPanel.add(showCarsButton);
        bottomPanel.add(bookIdLabel);
        bottomPanel.add(carIdField);
        bottomPanel.add(bookButton);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createStyledButton(String text, Font font, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        return button;
    }

    private JButton createBottomButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.DARK_GRAY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setFocusPainted(false);
        button.addActionListener(actionListener);
        return button;
    }

    void showCars() {
        List<String> availableCars = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JTextArea textArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);

        try {
            // Explicitly load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 1. Establish Database Connection (replace with your credentials)
            String url = "jdbc:mysql://localhost:3306/car_rental";
            String username = "root";
            String password = "Nas@57638";
            connection = DriverManager.getConnection(url, username, password);

            // 2. Create SQL Query
            String sql = "SELECT name, model, type FROM vehicles WHERE is_available = TRUE";
            preparedStatement = connection.prepareStatement(sql);

            // 3. Execute the Query
            resultSet = preparedStatement.executeQuery();

            // 4. Process the Results
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String model = resultSet.getString("model");
                String type = resultSet.getString("type");
                availableCars.add(name + " " + model + " (" + type + ")");
                textArea.append(name + " " + model + " (" + type + ")\n");
            }

            // 5. Display the Information
            if (availableCars.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No cars are currently available.", "Available Cars", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, scrollPane, "Available Cars", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading JDBC driver: " + e.getMessage(), "Driver Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return; // Exit the method if the driver couldn't be loaded
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching available cars: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Consider logging the error
        } finally {
            // Close resources in a finally block to ensure they are always closed
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    void bookCar(int carId) {
        JOptionPane.showMessageDialog(this, "Placeholder: Booking car with ID: " + carId);
    }

    void openVehicleRegistrationUI() {
        new VehicleRegistrationUI();
    }

    void openVehicleTypeSelectionUI() {
        new RentVehicleUI();
    }

    void openReturnVehicleUI() {
        new ReturnVehicleUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CarRentUI::new);
    }
}