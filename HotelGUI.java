// Enhanced Hotel Reservation System GUI (without CSV export)
package hotelreservation;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

class Room implements Serializable {
    int roomNumber;
    String type;
    double price;
    boolean isBooked;
    String bookingDate;

    Room(int roomNumber, String type, double price) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.isBooked = false;
        this.bookingDate = "";
    }

    public String toString() {
        return "Room " + roomNumber + " - " + type + " - PKR " + price + (isBooked ? " (Booked)" : "");
    }
}

class Booking implements Serializable {
    String customerName;
    String cnic;
    int roomNumber;
    String type;
    String date;
    double price;

    Booking(String customerName, String cnic, int roomNumber, String type, String date, double price) {
        this.customerName = customerName;
        this.cnic = cnic;
        this.roomNumber = roomNumber;
        this.type = type;
        this.date = date;
        this.price = price;
    }

    public String toString() {
        return customerName + " (CNIC: " + cnic + ") - Room " + roomNumber + " (" + type + ") on " + date + " - PKR " + price;
    }
}

public class HotelGUI extends JFrame {
    private static final List<Room> rooms = new ArrayList<>();
    private static List<Booking> bookings = new ArrayList<>();
    private JTextArea displayArea;

    public HotelGUI() {
        setTitle("Hotel Reservation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        initRooms();
        loadBookings();
        initUI();
    }

    private void initRooms() {
        rooms.add(new Room(101, "Standard", 5000));
        rooms.add(new Room(102, "Standard", 5000));
        rooms.add(new Room(201, "Deluxe", 8000));
        rooms.add(new Room(202, "Deluxe", 8000));
        rooms.add(new Room(301, "Suite", 12000));
        rooms.add(new Room(302, "Suite", 12000));
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        JButton viewBtn = new JButton("View Rooms");
        JButton bookBtn = new JButton("Book Room");
        JButton cancelBtn = new JButton("Cancel Booking");
        JButton viewBookingsBtn = new JButton("View Bookings");
        JButton historyByCNICBtn = new JButton("Booking History by CNIC");
        JButton filterBtn = new JButton("Filter by Price");

        viewBtn.addActionListener(e -> viewAvailableRooms());
        bookBtn.addActionListener(e -> bookRoom());
        cancelBtn.addActionListener(e -> cancelBooking());
        viewBookingsBtn.addActionListener(e -> viewBookings());
        historyByCNICBtn.addActionListener(e -> bookingHistoryByCNIC());
        filterBtn.addActionListener(e -> filterRoomsByPrice());

        buttonPanel.add(viewBtn);
        buttonPanel.add(bookBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(viewBookingsBtn);
        buttonPanel.add(historyByCNICBtn);
        buttonPanel.add(filterBtn);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(displayArea);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
    }

    private void viewAvailableRooms() {
        displayArea.setText("--- Available Rooms ---\n");
        for (Room r : rooms) {
            if (!r.isBooked) {
                displayArea.append(r + "\n");
            }
        }
    }

    private void bookRoom() {
        String name = JOptionPane.showInputDialog(this, "Enter your name:");
        if (name == null || name.isEmpty()) return;

        String cnic = JOptionPane.showInputDialog(this, "Enter CNIC (13 digits):");
        if (!cnic.matches("\\d{13}")) {
            JOptionPane.showMessageDialog(this, "Invalid CNIC.");
            return;
        }

        String[] types = {"Standard", "Deluxe", "Suite"};
        String type = (String) JOptionPane.showInputDialog(this, "Select Room Type:",
                "Room Type", JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
        if (type == null) return;

        String date = JOptionPane.showInputDialog(this, "Enter date (e.g., 15-06-2025):");
        if (!date.matches("\\d{2}-\\d{2}-\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Invalid date format.");
            return;
        }

        for (Room r : rooms) {
            if (r.type.equalsIgnoreCase(type) && !r.isBooked) {
                r.isBooked = true;
                r.bookingDate = date;
                Booking b = new Booking(name, cnic, r.roomNumber, r.type, date, r.price);
                bookings.add(b);
                simulatePayment(r.price);
                JOptionPane.showMessageDialog(this, "Room " + r.roomNumber + " booked successfully!");
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "No available rooms of type " + type);
    }

    private void cancelBooking() {
        String name = JOptionPane.showInputDialog(this, "Enter your name:");
        if (name == null || name.isEmpty()) return;

        Iterator<Booking> itr = bookings.iterator();
        while (itr.hasNext()) {
            Booking b = itr.next();
            if (b.customerName.equalsIgnoreCase(name)) {
                itr.remove();
                for (Room r : rooms) {
                    if (r.roomNumber == b.roomNumber) {
                        r.isBooked = false;
                        r.bookingDate = "";
                        break;
                    }
                }
                JOptionPane.showMessageDialog(this, "Booking for room " + b.roomNumber + " cancelled.");
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "No booking found under this name.");
    }

    private void viewBookings() {
        displayArea.setText("--- All Bookings ---\n");
        for (Booking b : bookings) {
            displayArea.append(b + "\n");
        }
    }

    private void bookingHistoryByCNIC() {
        String cnic = JOptionPane.showInputDialog(this, "Enter CNIC:");
        displayArea.setText("--- Booking History ---\n");
        for (Booking b : bookings) {
            if (b.cnic.equals(cnic)) {
                displayArea.append(b + "\n");
            }
        }
    }

    private void filterRoomsByPrice() {
        String minInput = JOptionPane.showInputDialog(this, "Enter minimum price:");
        String maxInput = JOptionPane.showInputDialog(this, "Enter maximum price:");
        try {
            double min = Double.parseDouble(minInput);
            double max = Double.parseDouble(maxInput);
            displayArea.setText("--- Rooms in Price Range ---\n");
            for (Room r : rooms) {
                if (!r.isBooked && r.price >= min && r.price <= max) {
                    displayArea.append(r + "\n");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input for price range.");
        }
    }

    private void simulatePayment(double price) {
        JOptionPane.showMessageDialog(this, "Processing payment of PKR " + price);
    }

    private void loadBookings() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("bookings.dat"))) {
            bookings = (List<Booking>) ois.readObject();
            for (Booking b : bookings) {
                for (Room r : rooms) {
                    if (r.roomNumber == b.roomNumber) {
                        r.isBooked = true;
                        r.bookingDate = b.date;
                    }
                }
            }
        } catch (Exception e) {}
    }

    private void saveBookings() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("bookings.dat"))) {
            oos.writeObject(bookings);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save bookings.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HotelGUI gui = new HotelGUI();
            gui.setVisible(true);
            gui.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    gui.saveBookings();
                }
            });
        });
    }
}
