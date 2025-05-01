import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VehicleRegistrationUI extends JFrame {

    JTextField nameField;
    JTextField modelField;
    JTextField priceField;
    JComboBox<String> typeComboBox;
    JTextArea outputArea;

    public VehicleRegistrationUI() {
        setTitle("Register Vehicle");
        setSize(400, 300); // Increased size to accommodate all fields
        setLayout(new GridLayout(0, 2, 5, 5));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel nameLabel = new JLabel("Vehicle Name:");
        nameField = new JTextField(20);

        JLabel modelLabel = new JLabel("Vehicle Model:");
        modelField = new JTextField(20);

        JLabel typeLabel = new JLabel("Vehicle Type:");
        String[] vehicleTypes = {"Car", "Bike", "Scooty"};
        typeComboBox = new JComboBox<>(vehicleTypes);

        JLabel priceLabel = new JLabel("Rental Price:");
        priceField = new JTextField(10);

        JButton registerButton = new JButton("Register Vehicle");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String model = modelField.getText();
                String type = (String) typeComboBox.getSelectedItem();
                String priceStr = priceField.getText();

                try {
                    double price = Double.parseDouble(priceStr);
                    Connection connection = null;
                    PreparedStatement preparedStatement = null;
                    try {
                        String url = "jdbc:mysql://localhost:3306/car_rental";
                        String username = "root";
                        String password = "Nas@57638";
                        connection = DriverManager.getConnection(url, username, password);

                        String sql = "INSERT INTO vehicles (name, model, type, rental_price, is_available) VALUES (?, ?, ?, ?, ?)";
                        preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1, name);
                        preparedStatement.setString(2, model);
                        preparedStatement.setString(3, type);
                        preparedStatement.setDouble(4, price);
                        preparedStatement.setBoolean(5, true); // Default to available

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            outputArea.setText("Vehicle registered successfully.");
                        } else {
                            outputArea.setText("Failed to register vehicle.");
                        }

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(VehicleRegistrationUI.this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } finally {
                        try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                        try { if (connection != null) connection.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                    }

                } catch (NumberFormatException ex) {
                    outputArea.setText("Invalid price format.");
                }
            }
        });

        outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(nameLabel);
        add(nameField);
        add(modelLabel);
        add(modelField);
        add(typeLabel);
        add(typeComboBox);
        add(priceLabel);
        add(priceField);
        add(new JLabel("")); // Empty label for spacing
        add(registerButton);
        add(new JLabel("")); // Empty label for spacing
        add(scrollPane);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VehicleRegistrationUI::new);
    }
}