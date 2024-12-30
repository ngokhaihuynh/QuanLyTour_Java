package Thongke;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import database.DatabaseConnection;

public class RevenuePanel extends JPanel {
    private JComboBox<String> cboMonthFilter;
    private JButton btnGenerateReport;
    private JTextArea txtReport;
    private JLabel lblTotalRevenue;

    public RevenuePanel() {
        setLayout(new BorderLayout(20, 20));

        // Panel cho phần lọc doanh thu theo tháng
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblMonthFilter = new JLabel("Lọc theo tháng:");

        cboMonthFilter = new JComboBox<>();
        cboMonthFilter.addItem("Tất cả");
        for (int i = 1; i <= 12; i++) {
            cboMonthFilter.addItem(String.format("%02d", i));
        }

        filterPanel.add(lblMonthFilter);
        filterPanel.add(cboMonthFilter);

        // Thêm filterPanel vào vị trí Bắc của BorderLayout
        add(filterPanel, BorderLayout.NORTH);

        // Panel cho phần tạo báo cáo và tổng doanh thu
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));

        // Tạo nút báo cáo
        btnGenerateReport = new JButton("Tạo báo cáo");
        btnGenerateReport.setAlignmentX(Component.CENTER_ALIGNMENT);  // Căn giữa nút
        btnGenerateReport.setBackground(new Color(30, 144, 255)); // Màu xanh dương
        btnGenerateReport.setForeground(Color.WHITE);            // Chữ trắng
        btnGenerateReport.setFont(new Font("Arial", Font.BOLD, 14));

        btnGenerateReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        lblTotalRevenue = new JLabel("Tổng doanh thu: 0 VND");
        lblTotalRevenue.setAlignmentX(Component.CENTER_ALIGNMENT);  // Căn giữa nhãn tổng doanh thu

        reportPanel.add(btnGenerateReport);
        reportPanel.add(Box.createVerticalStrut(10));  // Khoảng cách giữa các thành phần
        reportPanel.add(lblTotalRevenue);

        // Thêm reportPanel vào phần trung tâm
        add(reportPanel, BorderLayout.CENTER);

        // TextArea để hiển thị chi tiết báo cáo
        txtReport = new JTextArea(10, 30);
        txtReport.setEditable(false);
        txtReport.setLineWrap(true);  // Cho phép xuống dòng tự động
        txtReport.setWrapStyleWord(true);  // Giữ nguyên từ khi xuống dòng
        JScrollPane scrollPane = new JScrollPane(txtReport);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void generateReport() {
        String selectedMonth = (String) cboMonthFilter.getSelectedItem();
        String query;
        double totalAmount = 0;
        StringBuilder report = new StringBuilder();

        if (selectedMonth.equals("Tất cả")) {
            // Truy vấn tổng doanh thu của tất cả các thanh toán
            query = "SELECT SUM(amount) AS total_amount FROM payments";
            report.append("Báo cáo doanh thu tất cả:\n");
        } else {
            // Truy vấn doanh thu theo tháng
            query = "SELECT SUM(amount) AS total_amount FROM payments WHERE MONTH(payment_date) = ?";
            report.append("Báo cáo doanh thu tháng ").append(selectedMonth).append(":\n");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (!selectedMonth.equals("Tất cả")) {
                // Nếu lọc theo tháng, đặt tham số tháng
                stmt.setInt(1, Integer.parseInt(selectedMonth));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalAmount = rs.getDouble("total_amount");

                    // Kiểm tra kết quả có phải là NULL không
                    if (totalAmount == 0) {
                        report.append("Không có dữ liệu thanh toán cho tháng này hoặc doanh thu là 0.\n");
                    } else {
                        report.append("Tổng doanh thu: ").append(totalAmount).append(" VND\n");
                    }
                } else {
                    report.append("Không có dữ liệu thanh toán cho tháng này.\n");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Có lỗi khi lấy dữ liệu.");
        }

        // Cập nhật doanh thu trên giao diện
        txtReport.setText(report.toString());
        lblTotalRevenue.setText("Tổng doanh thu: " + totalAmount + " VND");
    }
}
