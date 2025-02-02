package Booking;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddEditBookingForm extends JFrame {
    private JTextField txtCustomerName;
    private JComboBox<String> cboDestination;
    private JComboBox<String> cboGuide;
    private JComboBox<String> cboTour;
    private JTextField txtTotalPrice;
    private JComboBox<String> cboPaymentStatus;  // Thêm ComboBox để chọn trạng thái thanh toán
    private JButton btnSaveBooking;
    private BookingManagement bookingManagementPanel;
    private JTextField txtPhoneNumber;
    private int bookingId;

    public AddEditBookingForm(BookingManagement bookingManagementPanel) {
        this.bookingId = -1; // Thêm mới
        this.bookingManagementPanel = bookingManagementPanel;
        initUI();
        setLocationRelativeTo(null);
    }

    public AddEditBookingForm(BookingManagement bookingManagementPanel, int bookingId, String customerName, String tourName,
                              String bookingDate, String paymentStatus, String destinationName, String guideName, double totalPrice) {
        this.bookingId = bookingId; // Chỉnh sửa
        this.bookingManagementPanel = bookingManagementPanel;
        initUI();

        txtCustomerName.setText(customerName);
        cboDestination.setSelectedItem(destinationName);
        cboTour.setSelectedItem(tourName);
        cboGuide.setSelectedItem(guideName);
        cboPaymentStatus.setSelectedItem(paymentStatus);  // Cập nhật trạng thái thanh toán
        txtTotalPrice.setText(String.valueOf(totalPrice));
    }

    private void initUI() {
        setTitle("Thêm/Sửa Đặt Tour");
        setSize(400, 480);  // Tăng kích thước form để có đủ không gian cho số điện thoại
        setLayout(new GridLayout(8, 2));  // Cập nhật số dòng trong GridLayout

        JLabel lblCustomerName = new JLabel("Tên khách hàng:");
        txtCustomerName = new JTextField();
        JLabel lblPhoneNumber = new JLabel("Số điện thoại:");  // Thêm nhãn số điện thoại
        txtPhoneNumber = new JTextField();  // Trường nhập số điện thoại
        JLabel lblDestination = new JLabel("Điểm đến:");
        cboDestination = new JComboBox<>();
        JLabel lblGuide = new JLabel("Hướng dẫn viên:");
        cboGuide = new JComboBox<>();
        JLabel lblTour = new JLabel("Tour:");
        cboTour = new JComboBox<>();
        JLabel lblTotalPrice = new JLabel("Tổng tiền:");
        txtTotalPrice = new JTextField();
        txtTotalPrice.setEditable(false);

        // Thêm JLabel và JComboBox cho trạng thái thanh toán
        JLabel lblPaymentStatus = new JLabel("Trạng thái thanh toán:");
        cboPaymentStatus = new JComboBox<>(new String[]{"Chưa thanh toán", "Đã thanh toán"});

        btnSaveBooking = new JButton("Lưu Đặt Tour");

        // Thêm vào form
        add(lblCustomerName);
        add(txtCustomerName);
        add(lblPhoneNumber);  // Thêm vào form
        add(txtPhoneNumber);  // Thêm trường nhập số điện thoại
        add(lblDestination);
        add(cboDestination);
        add(lblGuide);
        add(cboGuide);
        add(lblTour);
        add(cboTour);
        add(lblTotalPrice);
        add(txtTotalPrice);

        // Thêm phần trạng thái thanh toán vào form
        add(lblPaymentStatus);
        add(cboPaymentStatus);

        add(btnSaveBooking);

        loadDestinations();
        loadGuides();

        cboDestination.addActionListener(e -> loadToursByDestination());
        cboTour.addActionListener(e -> updateTotalPrice());
        btnSaveBooking.addActionListener(e -> saveBooking());

        setVisible(true);
    }
    private void loadDestinations() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM destinations";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String destinationName = rs.getString("destination_name");
                cboDestination.addItem(destinationName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGuides() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM guides WHERE status = 'Trống'";  // Lọc chỉ những hướng dẫn viên có trạng thái "Trống"
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String guideName = rs.getString("full_name");
                cboGuide.addItem(guideName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//   private void loadToursByDestination() {
//        String selectedDestination = (String) cboDestination.getSelectedItem();
//        if (selectedDestination == null) return;
//
//        try (Connection conn = DatabaseConnection.getConnection()) {
//
//            String sql = "SELECT id_destination FROM destinations WHERE destination_name = ?";
//            PreparedStatement pstmt = conn.prepareStatement(sql);
//            pstmt.setString(1, selectedDestination);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                int destinationId = rs.getInt("id_destination");
//
//                // Lấy các tour có trạng thái là "trống" (hoặc trạng thái bạn xác định là trống)
//                String tourSql = "SELECT * FROM tours WHERE id_destination = ? AND status = 'Trống'";
//                pstmt = conn.prepareStatement(tourSql);
//                pstmt.setInt(1, destinationId);
//                rs = pstmt.executeQuery();
//
//                cboTour.removeAllItems();
//                while (rs.next()) {
//                    String tourName = rs.getString("tour_name");
//                    cboTour.addItem(tourName);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    private void loadToursByDestination() {
        String selectedDestination = (String) cboDestination.getSelectedItem();
        if (selectedDestination == null) return;

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Lấy id_destination dựa trên tên điểm đến
            String sql = "SELECT id_destination FROM destinations WHERE destination_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, selectedDestination);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int destinationId = rs.getInt("id_destination");

                // Lấy tất cả các tour có id_destination tương ứng
                String tourSql = "SELECT * FROM tours WHERE id_destination = ?";
                pstmt = conn.prepareStatement(tourSql);
                pstmt.setInt(1, destinationId);
                rs = pstmt.executeQuery();

                cboTour.removeAllItems();
                while (rs.next()) {
                    String tourName = rs.getString("tour_name");
                    cboTour.addItem(tourName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTotalPrice() {
        int selectedTourIndex = cboTour.getSelectedIndex();
        if (selectedTourIndex == -1) return;

        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = "SELECT price FROM tours WHERE id_tour = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, selectedTourIndex + 1);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double totalPrice = rs.getDouble("price");
                txtTotalPrice.setText(String.valueOf(totalPrice));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveBooking() {
        String customerName = txtCustomerName.getText();
        double totalPrice = Double.parseDouble(txtTotalPrice.getText());
        String selectedDestination = (String) cboDestination.getSelectedItem();
        int selectedTourIndex = cboTour.getSelectedIndex();
        int selectedGuideIndex = cboGuide.getSelectedIndex();
        String paymentStatus = (String) cboPaymentStatus.getSelectedItem();  // Lấy trạng thái thanh toán

        // Kiểm tra xem tên khách hàng có trống không
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng");
            return;
        }

        // Kiểm tra nếu có ID hướng dẫn viên hợp lệ
        if (selectedGuideIndex == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hướng dẫn viên");
            return;
        }

        // Thay đổi giá trị của paymentStatus cho phù hợp với ENUM trong cơ sở dữ liệu
        if (paymentStatus.equals("Chưa thanh toán")) {
            paymentStatus = "Chưa thanh toán";
        } else if (paymentStatus.equals("Đã thanh toán")) {
            paymentStatus = "Đã thanh toán";
        }

        int destinationId = -1;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id_destination FROM destinations WHERE destination_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, selectedDestination);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        destinationId = rs.getInt("id_destination");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Nếu không tìm thấy điểm đến hợp lệ
        if (destinationId == -1) {
            JOptionPane.showMessageDialog(this, "Điểm đến không hợp lệ.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) { // Dùng một biến conn duy nhất
            PreparedStatement pstmt;
            ResultSet rs;

            // Kiểm tra khách hàng
            String checkCustomerSql = "SELECT id_customer FROM customers WHERE full_name = ?";
            try (PreparedStatement pstmtCheckCustomer = conn.prepareStatement(checkCustomerSql)) {
                pstmtCheckCustomer.setString(1, customerName);
                try (ResultSet rsCheckCustomer = pstmtCheckCustomer.executeQuery()) {
                    int customerId = -1;
                    if (rsCheckCustomer.next()) {
                        customerId = rsCheckCustomer.getInt("id_customer");
                    } else {
                        // Thêm khách hàng mới vào cơ sở dữ liệu
                        String insertCustomerSql = "INSERT INTO customers (full_name, phone_number) VALUES (?, ?)";
                        try (PreparedStatement pstmtInsertCustomer = conn.prepareStatement(insertCustomerSql, Statement.RETURN_GENERATED_KEYS)) {
                            pstmtInsertCustomer.setString(1, customerName);
                            pstmtInsertCustomer.setString(2, txtPhoneNumber.getText()); // Lấy số điện thoại từ JTextField
                            pstmtInsertCustomer.executeUpdate();
                            try (ResultSet rsInsertCustomer = pstmtInsertCustomer.getGeneratedKeys()) {
                                if (rsInsertCustomer.next()) {
                                    customerId = rsInsertCustomer.getInt(1);
                                }
                            }
                        }
                    }

                    // Kiểm tra xem hướng dẫn viên có tồn tại trong bảng guides không
                    String selectedGuideName = (String) cboGuide.getSelectedItem();  // Lấy tên hướng dẫn viên
                    if (selectedGuideName == null || selectedGuideName.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Vui lòng chọn hướng dẫn viên");
                        return;
                    }

                    int guideId = -1;
                    String sqlGuide = "SELECT id_guide FROM guides WHERE full_name = ?";
                    try (PreparedStatement pstmtGuide = conn.prepareStatement(sqlGuide)) {
                        pstmtGuide.setString(1, selectedGuideName);
                        try (ResultSet rsGuide = pstmtGuide.executeQuery()) {
                            if (rsGuide.next()) {
                                guideId = rsGuide.getInt("id_guide");
                            }
                        }
                    }

                    if (guideId == -1) {
                        JOptionPane.showMessageDialog(this, "Hướng dẫn viên không hợp lệ.");
                        return;
                    }

                    // Thêm booking vào cơ sở dữ liệu
                    String insertBookingSql = "INSERT INTO bookings (id_customer, id_tour, id_guide, total_price, booking_date, payment_status, id_destination) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmtInsertBooking = conn.prepareStatement(insertBookingSql, Statement.RETURN_GENERATED_KEYS)) {
                        pstmtInsertBooking.setInt(1, customerId);
                        pstmtInsertBooking.setInt(2, selectedTourIndex + 1);  // Cập nhật id_tour
                        pstmtInsertBooking.setInt(3, guideId);  // Sử dụng id_guide
                        pstmtInsertBooking.setDouble(4, totalPrice);
                        pstmtInsertBooking.setDate(5, new java.sql.Date(System.currentTimeMillis()));
                        pstmtInsertBooking.setString(6, paymentStatus);  // Lưu trạng thái thanh toán
                        pstmtInsertBooking.setInt(7, destinationId);  // Cập nhật id_destination

                        pstmtInsertBooking.executeUpdate();

                        // Cập nhật trạng thái tour sang "Đang diễn ra"
                        String updateTourStatusSql = "UPDATE tours SET status = 'Đang diễn ra' WHERE id_tour = ?";
                        try (PreparedStatement pstmtUpdateTourStatus = conn.prepareStatement(updateTourStatusSql)) {
                            pstmtUpdateTourStatus.setInt(1, selectedTourIndex + 1);
                            pstmtUpdateTourStatus.executeUpdate();
                        }

                        // Cập nhật trạng thái hướng dẫn viên thành "Đang làm việc"
                        String updateGuideStatusSql = "UPDATE guides SET status = 'Đang làm việc' WHERE id_guide = ?";
                        try (PreparedStatement pstmtUpdateGuideStatus = conn.prepareStatement(updateGuideStatusSql)) {
                            pstmtUpdateGuideStatus.setInt(1, guideId);
                            pstmtUpdateGuideStatus.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(this, "Booking thành công!");

                        // Tải lại dữ liệu vào form quản lý sau khi lưu thành công
                        bookingManagementPanel.loadBookings();

                        // Đóng form sau khi lưu thành công
                        dispose();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    


}
