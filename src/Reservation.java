/**
 * Reservation Data Model (POJO)
 */
public class Reservation {
    private int reservationId;
    private String guestName;
    private String guestPhone;
    private int roomNumber;
    private String category;
    private int nights;
    private double totalAmount;

    public Reservation(int reservationId, String guestName, String guestPhone,
                       int roomNumber, String category, int nights, double totalAmount) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.roomNumber = roomNumber;
        this.category = category;
        this.nights = nights;
        this.totalAmount = totalAmount;
    }

    // Getters
    public int getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getGuestPhone() { return guestPhone; }
    public int getRoomNumber() { return roomNumber; }
    public String getCategory() { return category; }
    public int getNights() { return nights; }
    public double getTotalAmount() { return totalAmount; }
}