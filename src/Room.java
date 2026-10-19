/**
 * Room Data Model (POJO)
 */
public class Room {
    private int roomNumber;
    private String category;
    private double pricePerNight;
    private boolean isBooked;

    public Room(int roomNumber, String category, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.isBooked = false;
    }

    public Room(int roomNumber, String category, double pricePerNight, boolean isBooked) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.isBooked = isBooked;
    }

    // Getters and Setters
    public int getRoomNumber() { return roomNumber; }
    public String getCategory() { return category; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isBooked() { return isBooked; }
}