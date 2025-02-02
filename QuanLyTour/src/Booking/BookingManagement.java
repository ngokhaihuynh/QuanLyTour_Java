package Booking;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class BookingManagement extends JPanel {
    private JTable tableBookings;
    private JButton btnAddBooking;
    private JButton btnEditBooking;
    private JButton btnDeleteBooking;
    private JButton btnPayment;  // Thêm nút thanh toán

    public BookingManagement() {
        setLayout(new BorderLayout());

        tableBookings = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableBookings);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new FlowLayout());

        btnAddBooking = new JButton("Thêm Đặt Tour");
        btnAddBooking.setBackground(new Color(50, 205, 50)); // Xanh lá
        btnAddBooking.setForeground(Color.WHITE);

        btnEditBooking = new JButton("Chỉnh Sửa");
        btnEditBooking.setBackground(new Color(30, 144, 255)); // Xanh dương
        btnEditBooking.setForeground(Color.WHITE);

        btnDeleteBooking = new JButton("Xóa Đặt Tour");
        btnDeleteBooking.setBackground(new Color(220, 20, 60)); // Đỏ
        btnDeleteBooking.setForeground(Color.WHITE);

        btnPayment = new JButton("Thanh Toán");
        btnPayment.setBackground(new Color(255, 165, 0)); // Cam
        btnPayment.setForeground(Color.WHITE);

        // Thêm các nút vào panelButtons
        panelButtons.add(btnAddBooking);
        panelButtons.add(btnEditBooking);
        panelButtons.add(btnDeleteBooking);
        panelButtons.add(btnPayment);


        add(panelButtons, BorderLayout.SOUTH);

        loadBookings();

        btnAddBooking.addActionListener(e -> openAddBookingForm());
        btnEditBooking.addActionListener(e -> openEditBookingForm());
        btnDeleteBooking.addActionListener(e -> deleteBooking());
        btnPayment.addActionListener(e -> openPaymentForm());  // Xử lý sự kiện nút thanh toán
    }

    public void loadBookings() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT b.id_booking, c.full_name AS customer_name, t.tour_name, b.total_price, " +
                         "b.booking_date, b.payment_status, d.destination_name, g.full_name AS guide_name " +
                         "FROM bookings b " +
                         "JOIN customers c ON b.id_customer = c.id_customer " +
                         "JOIN tours t ON b.id_tour = t.id_tour " +
                         "JOIN destinations d ON t.id_destination = d.id_destination " +
                         "JOIN guides g ON b.id_guide = g.id_guide";

            ResultSet rs = stmt.executeQuery(sql);

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID Booking");
            model.addColumn("Tên Khách Hàng");
            model.addColumn("Tour");
            model.addColumn("Tổng Tiền");
            model.addColumn("Ngày Đặt");
            model.addColumn("Trạng Thái Thanh Toán");
            model.addColumn("Điểm Đến");
            model.addColumn("Hướng Dẫn Viên");

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id_booking"));
                row.add(rs.getString("customer_name"));
                row.add(rs.getString("tour_name"));
                row.add(rs.getString("total_price"));
                row.add(rs.getString("booking_date"));
                row.add(rs.getString("payment_status"));
                row.add(rs.getString("destination_name"));
                row.add(rs.getString("guide_name"));
                model.addRow(row);
            }

            tableBookings.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openAddBookingForm() {
        AddEditBookingForm addForm = new AddEditBookingForm(this); 
        addForm.setVisible(true); 
    }

    private void openEditBookingForm() {
        int selectedRow = tableBookings.getSelectedRow();
        if (selectedRow != -1) {
            int bookingId = Integer.parseInt((String) tableBookings.getValueAt(selectedRow, 0));
            String customerName = (String) tableBookings.getValueAt(selectedRow, 1);
            String tourName = (String) tableBookings.getValueAt(selectedRow, 2);
            double totalPrice = Double.parseDouble((String) tableBookings.getValueAt(selectedRow, 3));
            String bookingDate = (String) tableBookings.getValueAt(selectedRow, 4);
            String paymentStatus = (String) tableBookings.getValueAt(selectedRow, 5);
            String destinationName = (String) tableBookings.getValueAt(selectedRow, 6);
            String guideName = (String) tableBookings.getValueAt(selectedRow, 7);

            AddEditBookingForm editForm = new AddEditBookingForm(this, bookingId, customerName, tourName, bookingDate, paymentStatus, destinationName, guideName, totalPrice);
            editForm.setVisible(true); 
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một booking để chỉnh sửa");
        }
    }

    private void deleteBooking() {
        int selectedRow = tableBookings.getSelectedRow();
        if (selectedRow != -1) {
            int bookingId = Integer.parseInt((String) tableBookings.getValueAt(selectedRow, 0));
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa đặt tour này?", "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    // Xóa các bản ghi trong bảng payments có id_booking tham chiếu đến id_booking của bảng bookings
                    String deletePaymentsSql = "DELETE FROM payments WHERE id_booking = ?";
                    PreparedStatement pstmtPayments = conn.prepareStatement(deletePaymentsSql);
                    pstmtPayments.setInt(1, bookingId);
                    pstmtPayments.executeUpdate();

                    // Sau đó xóa bản ghi trong bảng bookings
                    String deleteBookingSql = "DELETE FROM bookings WHERE id_booking = ?";
                    PreparedStatement pstmtBooking = conn.prepareStatement(deleteBookingSql);
                    pstmtBooking.setInt(1, bookingId);
                    pstmtBooking.executeUpdate();

                    loadBookings();  
                    JOptionPane.showMessageDialog(this, "Xóa đặt tour thành công!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một booking để xóa");
        }
    }

    private void openPaymentForm() {
        int selectedRow = tableBookings.getSelectedRow();
        if (selectedRow != -1) {
            int bookingId = Integer.parseInt((String) tableBookings.getValueAt(selectedRow, 0));
            String customerName = (String) tableBookings.getValueAt(selectedRow, 1);
            double totalPrice = Double.parseDouble((String) tableBookings.getValueAt(selectedRow, 3));

            // Truyền đối tượng bookingManagement vào PaymentForm
            PaymentForm paymentForm = new PaymentForm(bookingId, customerName, totalPrice, this);
            paymentForm.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một booking để thanh toán");
        }
    }
}
