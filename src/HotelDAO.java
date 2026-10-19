import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all SQL database operations (CRUD).
 */
public class HotelDAO {

    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, category, price_per_night) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, room.getRoomNumber());
            pstmt.setString(2, room.getCategory());
            pstmt.setDouble(3, room.getPricePerNight());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error adding room: " + e.getMessage());
            return false;
        }
    }

    public List<Room> getRooms(boolean onlyAvailable) {
        List<Room> rooms = new ArrayList<>();
        String sql = onlyAvailable
                ? "SELECT * FROM rooms WHERE is_booked = FALSE"
                : "SELECT * FROM rooms";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(new Room(
                        rs.getInt("room_number"),
                        rs.getString("category"),
                        rs.getDouble("price_per_night"),
                        rs.getBoolean("is_booked")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching rooms: " + e.getMessage());
        }
        return rooms;
    }

    public boolean bookRoom(String guestName, String phone, int roomNum, int nights) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1. Verify Room Availability
            String checkSql = "SELECT price_per_night, is_booked FROM rooms WHERE room_number = ?";
            double pricePerNight = 0;

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, roomNum);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("❌ Room #" + roomNum + " does not exist!");
                    return false;
                }
                if (rs.getBoolean("is_booked")) {
                    System.out.println("❌ Room #" + roomNum + " is currently OCCUPIED!");
                    return false;
                }
                pricePerNight = rs.getDouble("price_per_night");
            }

            // 2. Insert Guest
            String insertGuest = "INSERT INTO guests (name, phone) VALUES (?, ?)";
            int guestId = -1;
            try (PreparedStatement guestStmt = conn.prepareStatement(insertGuest, Statement.RETURN_GENERATED_KEYS)) {
                guestStmt.setString(1, guestName);
                guestStmt.setString(2, phone);
                guestStmt.executeUpdate();

                ResultSet keyRs = guestStmt.getGeneratedKeys();
                if (keyRs.next()) {
                    guestId = keyRs.getInt(1);
                }
            }

            // 3. Create Reservation
            double totalAmount = pricePerNight * nights;
            String insertRes = "INSERT INTO reservations (guest_id, room_number, nights, total_amount) VALUES (?, ?, ?, ?)";
            int reservationId = -1;
            try (PreparedStatement resStmt = conn.prepareStatement(insertRes, Statement.RETURN_GENERATED_KEYS)) {
                resStmt.setInt(1, guestId);
                resStmt.setInt(2, roomNum);
                resStmt.setInt(3, nights);
                resStmt.setDouble(4, totalAmount);
                resStmt.executeUpdate();

                ResultSet keyRs = resStmt.getGeneratedKeys();
                if (keyRs.next()) {
                    reservationId = keyRs.getInt(1);
                }
            }

            // 4. Update Room Status
            String updateRoom = "UPDATE rooms SET is_booked = TRUE WHERE room_number = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateRoom)) {
                updateStmt.setInt(1, roomNum);
                updateStmt.executeUpdate();
            }

            System.out.println("\n🎉 RESERVATION SUCCESSFUL!");
            System.out.println("Reservation ID : #" + reservationId);
            System.out.println("Guest Name     : " + guestName);
            System.out.println("Room Number    : " + roomNum);
            System.out.println("Duration       : " + nights + " night(s)");
            System.out.println("Total Bill     : $" + totalAmount);
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Booking failed: " + e.getMessage());
            return false;
        }
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = """
            SELECT r.reservation_id, g.name, g.phone, r.room_number, rm.category, r.nights, r.total_amount
            FROM reservations r
            JOIN guests g ON r.guest_id = g.guest_id
            JOIN rooms rm ON r.room_number = rm.room_number;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Reservation(
                        rs.getInt("reservation_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getInt("room_number"),
                        rs.getString("category"),
                        rs.getInt("nights"),
                        rs.getDouble("total_amount")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error loading reservations: " + e.getMessage());
        }
        return list;
    }

    public boolean checkOut(int resId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Find room associated with reservation
            String findRoomSql = "SELECT room_number FROM reservations WHERE reservation_id = ?";
            int roomNum = -1;

            try (PreparedStatement pstmt = conn.prepareStatement(findRoomSql)) {
                pstmt.setInt(1, resId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    roomNum = rs.getInt("room_number");
                } else {
                    System.out.println("❌ Reservation ID #" + resId + " not found!");
                    return false;
                }
            }

            // Remove reservation record
            String deleteResSql = "DELETE FROM reservations WHERE reservation_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteResSql)) {
                pstmt.setInt(1, resId);
                pstmt.executeUpdate();
            }

            // Mark room as available again
            String freeRoomSql = "UPDATE rooms SET is_booked = FALSE WHERE room_number = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(freeRoomSql)) {
                pstmt.setInt(1, roomNum);
                pstmt.executeUpdate();
            }

            System.out.println("✅ Check-out complete for Reservation #" + resId + ". Room #" + roomNum + " is now AVAILABLE!");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Check-out failed: " + e.getMessage());
            return false;
        }
    }
}