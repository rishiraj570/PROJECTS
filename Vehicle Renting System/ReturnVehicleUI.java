import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ReturnVehicleUI extends JFrame {

    JTextField vehicleIdField;
    JTextArea outputArea;

    public ReturnVehicleUI() {
        setTitle("Return a Vehicle");
        setSize(400, 200);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel returnIdLabel = new JLabel("Enter Vehicle ID to Return:");
        vehicleIdField = new JTextField(10);
        JButton returnButton = new JButton("Return Vehicle");
        outputArea = new JTextArea(5, 25);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String vehicleId = vehicleIdField.getText();
                try {
                    int idToReturn = Integer.parseInt(vehicleId);

                    // Replace with your actual DB details
                    String url = "jdbc:mysql://localhost:3306/car_rental";
                    String username = "root";
                    String password = "Nas@57638";

                    try (Connection conn = DriverManager.getConnection(url, username, password)) {
                        // First, check if vehicle exists and if it’s already available
                        String checkQuery = "SELECT is_available FROM vehicles WHERE vehicle_id = ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                            checkStmt.setInt(1, idToReturn);
                            ResultSet rs = checkStmt.executeQuery();

                            if (rs.next()) {
                                boolean isAvailable = rs.getBoolean("is_available");
                                if (isAvailable) {
                                    outputArea.setText("Vehicle with ID " + idToReturn + " is already available.");
                                } else {
                                    // Update vehicle to mark as returned
                                    String updateQuery = "UPDATE vehicles SET is_available = 1 WHERE vehicle_id = ?";
                                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                        updateStmt.setInt(1, idToReturn);
                                        int rowsUpdated = updateStmt.executeUpdate();

                                        if (rowsUpdated > 0) {
                                            outputArea.setText("Successfully returned vehicle with ID: " + idToReturn);
                                        } else {
                                            outputArea.setText("Error updating vehicle return.");
                                        }
                                    }
                                }
                            } else {
                                outputArea.setText("No vehicle found with ID: " + idToReturn);
                            }
                        }

                    } catch (SQLException dbEx) {
                        dbEx.printStackTrace();
                        outputArea.setText("Database error: " + dbEx.getMessage());
                    }

                } catch (NumberFormatException nfe) {
                    outputArea.setText("Invalid Vehicle ID format.");
                }
            }
        });

        add(returnIdLabel);
        add(vehicleIdField);
        add(returnButton);
        add(scrollPane);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ReturnVehicleUI::new);
    }
}
