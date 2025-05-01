import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class RentVehicleUI extends JFrame {

    JComboBox<String> typeComboBox;
    JTextField vehicleIdField;
    JTextField customerNameField;
    JTextField travelersField;
    JTextField destinationField;
    JTextArea outputArea;

    public RentVehicleUI() {
        setTitle("Rent a Vehicle");
        setSize(400, 300);
        setLayout(new GridLayout(0, 2, 5, 5));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JLabel typeLabel = new JLabel("Type of Vehicle:");
        String[] vehicleTypes = {"Car", "Bike", "Scooty"};
        typeComboBox = new JComboBox<>(vehicleTypes);

        JLabel vehicleIdLabel = new JLabel("Vehicle ID:");
        vehicleIdField = new JTextField(10);

        JLabel customerNameLabel = new JLabel("Customer Name:");
        customerNameField = new JTextField(20);

        JLabel travelersLabel = new JLabel("Traveling With (Count):");
        travelersField = new JTextField(5);

        JLabel destinationLabel = new JLabel("Destination:");
        destinationField = new JTextField(20);

        JButton rentButton = new JButton("Rent Vehicle");
        rentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String type = (String) typeComboBox.getSelectedItem();
                String vehicleIdText = vehicleIdField.getText().trim();
                String customerName = customerNameField.getText().trim();
                String travelers = travelersField.getText().trim();
                String destination = destinationField.getText().trim();

                if (vehicleIdText.isEmpty() || customerName.isEmpty() || travelers.isEmpty() || destination.isEmpty()) {
                    outputArea.setText("Please fill in all fields.");
                    return;
                }

                int vehicleId;
                try {
                    vehicleId = Integer.parseInt(vehicleIdText);
                } catch (NumberFormatException ex) {
                    outputArea.setText("Vehicle ID must be a number.");
                    return;
                }

                String url = "jdbc:mysql://localhost:3306/car_rental";
                String user = "root";
                String password = "Nas@57638";

                try (Connection conn = DriverManager.getConnection(url, user, password)) {
                    String checkQuery = "SELECT is_available FROM vehicles WHERE vehicle_id = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                        checkStmt.setInt(1, vehicleId);
                        ResultSet rs = checkStmt.executeQuery();

                        if (rs.next()) {
                            int isAvailable = rs.getInt("is_available");
                            if (isAvailable == 1) {
                                // Vehicle is available, mark it as rented
                                String updateQuery = "UPDATE vehicles SET is_available = 0 WHERE vehicle_id = ?";
                                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                    updateStmt.setInt(1, vehicleId);
                                    updateStmt.executeUpdate();

                                    String rentalInfo = "✅ Vehicle rented: " + type + ", ID: " + vehicleId + ", Customer: " + customerName + ", Travelers: " + travelers + ", Destination: " + destination;
                                    outputArea.setText(rentalInfo);
                                }
                            } else {
                                outputArea.setText("❌ Vehicle ID " + vehicleId + " is already rented.");
                            }
                        } else {
                            outputArea.setText("❌ Vehicle ID " + vehicleId + " not found.");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    outputArea.setText("⚠️ Database error: " + ex.getMessage());
                }
            }
        });

        add(typeLabel);
        add(typeComboBox);
        add(vehicleIdLabel);
        add(vehicleIdField);
        add(customerNameLabel);
        add(customerNameField);
        add(travelersLabel);
        add(travelersField);
        add(destinationLabel);
        add(destinationField);
        add(new JLabel(""));
        add(rentButton);
        add(new JLabel(""));
        add(scrollPane);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RentVehicleUI::new);
    }
}
