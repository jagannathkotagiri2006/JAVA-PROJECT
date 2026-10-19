/*
 * Guest Data Model (POJO)
 */
public class Guest {
    private int guestId;
    private String name;
    private String phone;

    public Guest(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Guest(int guestId, String name, String phone) {
        this.guestId = guestId;
        this.name = name;
        this.phone = phone;
    }

    // Getters
    public int getGuestId() { return guestId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
}
