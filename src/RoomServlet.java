import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// This annotation tells Tomcat which URL this class listens to!
@WebServlet("/api/rooms")
public class RoomServlet extends HttpServlet {

    // 1. Handle CORS (So your HTML frontend is allowed to connect)
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // 2. Handle GET requests (Fetching the rooms)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            // 1. We will build the JSON string manually using StringBuilder
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");

            // 2. 👉 CALL YOUR JDBC CODE HERE 👈
            // You need to call the method in your HotelDAO that returns all rooms.
            // For example:
            // 2. Fetch the rooms from your database
            HotelDAO dao = new HotelDAO();
            List<Room> roomList = dao.getRooms(false); // ⚠️ Change 'getAllRooms()' to whatever your method is actually named in HotelDAO!


            // 3. Loop through your rooms and convert them to JSON
            // (Uncomment and adjust this loop once you connect your DAO)

        for (int i = 0; i < roomList.size(); i++) {
            Room r = roomList.get(i);
            jsonBuilder.append("{");
            jsonBuilder.append("\"number\":\"").append(r.getRoomNumber()).append("\",");
            jsonBuilder.append("\"category\":\"").append(r.getCategory()).append("\",");
            jsonBuilder.append("\"price\":").append(r.getPricePerNight()).append(",");
            jsonBuilder.append("\"status\":\"").append(r.isBooked() ? "Booked" : "Available").append("\"");
            jsonBuilder.append("}");

            if (i < roomList.size() - 1) {
                jsonBuilder.append(","); // Add a comma between rooms
            }
        }


            jsonBuilder.append("]");

            // 4. Send the real JSON back to the website
            out.print(jsonBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("[]"); // Send empty array on error
        } finally {
            out.flush();
        }
    }



// ... inside your RoomServlet class ...

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setContentType("application/json");

        try {
            // 1. Read the JSON text sent by the webpage
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonText = sb.toString();

            // 2. Extract the values from the JSON string
            // (Since you aren't using Maven/Gson, we can extract them using basic String manipulation for now)
            String number = jsonText.split("\"number\":\"")[1].split("\"")[0];
            String category = jsonText.split("\"category\":\"")[1].split("\"")[0];
            String priceString = jsonText.split("\"price\":")[1].split(",")[0].replaceAll("[^0-9.]", "");
            double price = Double.parseDouble(priceString);
            int roomNum = Integer.parseInt(number);
            // 3. 👉 CALL YOUR JDBC CODE HERE 👈
            // Based on your screenshots, you have a HotelDAO or DatabaseConnection class.
            // You need to call your method that inserts a room.
            // For example (you may need to adjust the class/method names to match yours exactly):

             HotelDAO dao = new HotelDAO();
             dao.addRoom(new Room(roomNum, category, price));

            System.out.println("Success: Added room " + number + " to the database!");

            // 4. Send success message back to the website
            PrintWriter out = response.getWriter();
            out.print("{\"message\": \"Room saved to MySQL!\"}");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"Failed to save room\"}");
            out.flush();
        }
    }

    // Helper method for CORS headers
    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}