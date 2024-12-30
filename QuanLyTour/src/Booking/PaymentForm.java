package Booking;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PaymentForm extends JFrame {
    private JTextField txtAmount;
    private JComboBox<String> cboPaymentMethod;
    private JButton btnSavePayment;
    private int bookingId;
    private String customerName;
    private double totalPrice;
    private BookingManagement bookingManagement;  // Thêm đối tượng BookingManagement

    public PaymentForm(int bookingId, String customerName, double totalPrice, BookingManagement bookingManagement) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
        this.bookingManagement = bookingManagement;  // Lưu đối tượng BookingManagement
        initUI();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        setTitle("Thanh Toán");
        setSize(400, 250);
        setLocationRelativeTo(null);  // Đặt cửa sổ ở giữa màn hình

        // Thiết lập nền màu kem và layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));  // GridLayout với khoảng cách 10px
        panel.setBackground(new Color(255, 245, 225));  // Màu nền kem

        // Các thành phần giao diện
        JLabel lblAmount = new JLabel("Số Tiền:");
        txtAmount = new JTextField(String.valueOf(totalPrice));

        JLabel lblPaymentMethod = new JLabel("Phương Thức Thanh Toán:");
        cboPaymentMethod = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ", "Chuyển khoản"});

        btnSavePayment = new JButton("Lưu Thanh Toán");
        btnSavePayment.setBackground(new Color(34, 139, 34));  // Màu xanh lá cho nút
        btnSavePayment.setForeground(Color.WHITE);  // Màu chữ trắng

        // Thêm các thành phần vào panel
        panel.add(lblAmount);
        panel.add(txtAmount);
        panel.add(lblPaymentMethod);
        panel.add(cboPaymentMethod);
        panel.add(new JLabel());  // Để trống 1 ô
        panel.add(btnSavePayment);

        // Lắng nghe sự kiện nút lưu
        btnSavePayment.addActionListener(e -> savePayment());

        // Thêm panel vào cửa sổ
        add(panel);
        setVisible(true);
    }

    private void savePayment() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            double amount = Double.parseDouble(txtAmount.getText());
            String paymentMethod = (String) cboPaymentMethod.getSelectedItem();

            // Lưu thông tin thanh toán vào bảng payments
            String insertPaymentSQL = "INSERT INTO payments (id_booking, payment_date, amount, payment_method) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertPaymentSQL);
            pstmt.setInt(1, bookingId);
            pstmt.setDate(2, new java.sql.Date(System.currentTimeMillis()));  // Ngày thanh toán là ngày hiện tại
            pstmt.setDouble(3, amount);
            pstmt.setString(4, paymentMethod);
            pstmt.executeUpdate();

            // Cập nhật trạng thái thanh toán trong bảng bookings
            String updatePaymentStatusSQL = "UPDATE bookings SET payment_status = 'Đã thanh toán' WHERE id_booking = ?";
            pstmt = conn.prepareStatement(updatePaymentStatusSQL);
            pstmt.setInt(1, bookingId);
            pstmt.executeUpdate();

            // Cập nhật trạng thái tour sang 'Trống'
            String getTourIdSQL = "SELECT id_tour FROM bookings WHERE id_booking = ?";
            pstmt = conn.prepareStatement(getTourIdSQL);
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");

            // Gọi phương thức loadBookings() từ BookingManagement để tải lại dữ liệu
            bookingManagement.loadBookings();  // Tải lại bảng bookings

            // Đóng form thanh toán sau khi lưu và cập nhật trạng thái
            dispose();  
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi trong quá trình thanh toán!");
        }
    }

}
