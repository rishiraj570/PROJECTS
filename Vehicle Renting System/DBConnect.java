import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnect {
    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/car_rental";
        String user = "root"; // change if needed
        String pass = "Nas@57638";     // change if needed
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, pass);
    }
}
