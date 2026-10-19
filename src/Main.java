import java.util.List;
import java.util.Scanner;

/**
 * Main application runner with CLI Interface.
 */
public class Main {

    private static final HotelDAO dao = new HotelDAO();

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   HOTEL MANAGEMENT SYSTEM (MODULAR)      ");
        System.out.println("==========================================");

        // Auto-initialize DB tables on startup
        if (!DatabaseConnection.initializeDatabase()) {
            System.err.println("❌ Database setup failed. Please check MySQL credentials in DatabaseConnection.java.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n------------------------------------------");
            System.out.println("Select an Option:");
            System.out.println("1. Add a New Room");
            System.out.println("2. View All Rooms");
            System.out.println("3. View Available Rooms");
            System.out.println("4. Book a Room");
            System.out.println("5. View All Reservations");
            System.out.println("6. Check-Out Guest");
            System.out.println("7. Exit");
            System.out.print("Enter choice (1-7): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> handleAddRoom(scanner);
                    case 2 -> handleViewRooms(false);
                    case 3 -> handleViewRooms(true);
                    case 4 -> handleBookRoom(scanner);
                    case 5 -> handleViewReservations();
                    case 6 -> handleCheckOut(scanner);
                    case 7 -> {
                        running = false;
                        System.out.println("👋 Exiting system. Goodbye!");
                    }
                    default -> System.out.println("⚠️ Invalid choice! Please enter 1-7.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid input! Please enter a valid number.");
            }
        }

        scanner.close();
    }

    private static void handleAddRoom(Scanner scanner) {
        try {
            System.out.print("Enter Room Number: ");
            int roomNum = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter Category (Standard / Deluxe / Suite): ");
            String category = scanner.nextLine();

            System.out.print("Enter Price Per Night (Rs.): ");
            double price = Double.parseDouble(scanner.nextLine());

            if (dao.addRoom(new Room(roomNum, category, price))) {
                System.out.println("✅ Room #" + roomNum + " added successfully!");
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid numerical input.");
        }
    }

    private static void handleViewRooms(boolean onlyAvailable) {
        List<Room> rooms = dao.getRooms(onlyAvailable);
        System.out.println(onlyAvailable ? "\n--- AVAILABLE ROOMS ---" : "\n--- ALL ROOMS ---");

        if (rooms.isEmpty()) {
            System.out.println("No rooms found matching criteria.");
            return;
        }

        for (Room r : rooms) {
            System.out.printf("Room #%-4d | Category: %-10s | Price: $%-7.2f | Status: %s%n",
                    r.getRoomNumber(), r.getCategory(), r.getPricePerNight(),
                    (r.isBooked() ? "🔴 BOOKED" : "🟢 AVAILABLE"));
        }
    }

    private static void handleBookRoom(Scanner scanner) {
        try {
            System.out.print("Enter Guest Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Phone Number: ");
            String phone = scanner.nextLine();

            System.out.print("Enter Room Number to Book: ");
            int roomNum = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter Duration (Nights): ");
            int nights = Integer.parseInt(scanner.nextLine());

            dao.bookRoom(name, phone, roomNum, nights);

        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid numerical input.");
        }
    }

    private static void handleViewReservations() {
        List<Reservation> resList = dao.getAllReservations();
        System.out.println("\n--- ALL ACTIVE RESERVATIONS ---");

        if (resList.isEmpty()) {
            System.out.println("No active reservations found.");
            return;
        }

        for (Reservation r : resList) {
            System.out.printf("Res ID #%-5d | Guest: %-15s | Phone: %-12s | Room: #%-3d [%-8s] | Nights: %-2d | Total: Rs.%-8.2f%n",
                    r.getReservationId(), r.getGuestName(), r.getGuestPhone(),
                    r.getRoomNumber(), r.getCategory(), r.getNights(), r.getTotalAmount());
        }
    }

    private static void handleCheckOut(Scanner scanner) {
        try {
            System.out.print("Enter Reservation ID to Check-Out: ");
            int resId = Integer.parseInt(scanner.nextLine());
            dao.checkOut(resId);
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid reservation ID.");
        }
    }
}