import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connections and automatic schema creation.
 */
public class DatabaseConnection {

    // --- CONFIGURE YOUR MYSQL CREDENTIALS HERE ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";     // Your MySQL username
    private static final String DB_PASS = "Jagannath@2006"; // Your MySQL password

    public static Connection getConnection() throws SQLException {
        try {
            // This line explicitly loads the MySQL driver for Tomcat
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found! " + e.getMessage());
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static boolean initializeDatabase() {
        System.out.print("Connecting to database... ");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Rooms Table
            String createRoomsTable = """
                CREATE TABLE IF NOT EXISTS rooms (
                    room_number INT PRIMARY KEY,
                    category VARCHAR(50) NOT NULL,
                    price_per_night DECIMAL(10, 2) NOT NULL,
                    is_booked BOOLEAN DEFAULT FALSE
                );
            """;
            stmt.execute(createRoomsTable);

            // Create Guests Table
            String createGuestsTable = """
                CREATE TABLE IF NOT EXISTS guests (
                    guest_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    phone VARCHAR(20) NOT NULL
                );
            """;
            stmt.execute(createGuestsTable);

            // Create Reservations Table
            String createReservationsTable = """
                CREATE TABLE IF NOT EXISTS reservations (
                    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
                    guest_id INT NOT NULL,
                    room_number INT NOT NULL,
                    nights INT NOT NULL,
                    total_amount DECIMAL(10, 2) NOT NULL,
                    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (guest_id) REFERENCES guests(guest_id) ON DELETE CASCADE,
                    FOREIGN KEY (room_number) REFERENCES rooms(room_number) ON DELETE CASCADE
                );
            """;
            stmt.execute(createReservationsTable);

            System.out.println("✅ Connected & Tables Initialized!");
            return true;

        } catch (SQLException e) {
            System.err.println("\n❌ DB Connection Error: " + e.getMessage());
            return false;
        }
    }
}