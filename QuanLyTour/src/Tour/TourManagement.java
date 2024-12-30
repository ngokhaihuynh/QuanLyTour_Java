package Tour;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TourManagement extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Khởi tạo lớp TourManagement
                new TourManagement().setVisible(true);
            }
        });
    }

    public TourManagement() {
        setLayout(new BorderLayout());

        // Tạo các cột cho JTable (Cập nhật thêm cột "Mô tả")
        String[] columnNames = {"ID Tour", "Tên Tour", "Điểm đến", "Giá", "Ngày bắt đầu", "Ngày kết thúc", "Mô tả", "Trạng thái", "Hình ảnh"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddTour = new JButton("Thêm");
        JButton btnEditTour = new JButton("Sửa");
        JButton btnDeleteTour = new JButton("Xoá");

        // Định nghĩa nút "Thêm"
        btnAddTour.setBackground(new Color(50, 205, 50)); // Màu xanh lá cây
        btnAddTour.setForeground(Color.WHITE);           // Chữ trắng
        btnAddTour.setFont(new Font("Arial", Font.BOLD, 14));

        // Định nghĩa nút "Sửa"
        btnEditTour.setBackground(new Color(30, 144, 255)); // Màu xanh dương
        btnEditTour.setForeground(Color.WHITE);            // Chữ trắng
        btnEditTour.setFont(new Font("Arial", Font.BOLD, 14));

        // Định nghĩa nút "Xoá"
        btnDeleteTour.setBackground(new Color(220, 20, 60)); // Màu đỏ
        btnDeleteTour.setForeground(Color.WHITE);           // Chữ trắng
        btnDeleteTour.setFont(new Font("Arial", Font.BOLD, 14));


        // Thêm các nút vào panel
        buttonPanel.add(btnAddTour);
        buttonPanel.add(btnEditTour);
        buttonPanel.add(btnDeleteTour);


        // Thêm tiêu đề phía trên bảng
 
        JLabel lblTitle = new JLabel("Danh sách Tour", JLabel.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18)); // Đổi font thành đậm
        lblTitle.setForeground(new Color(34, 139, 34)); // Màu xanh lá (mã màu RGB: 34, 139, 34)
        add(lblTitle, BorderLayout.NORTH);


        // Thêm các nút vào vùng SOUTH
        add(buttonPanel, BorderLayout.SOUTH);

        // Load danh sách tour từ cơ sở dữ liệu
        loadTourData();

        // ActionListener cho nút Add Tour
        btnAddTour.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Tạo form thêm tour mới và hiển thị nó
                AddEditTourForm form = new AddEditTourForm(TourManagement.this);
                form.setVisible(true); // Đặt form hiển thị
            }
        });

        // ActionListener cho nút Edit Tour
        btnEditTour.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Lấy ID, Tên, Điểm đến, Giá, Ngày bắt đầu, Ngày kết thúc, Mô tả, Trạng thái, Hình ảnh từ bảng
                    int tourId = (int) table.getValueAt(selectedRow, 0);
                    String name = (String) table.getValueAt(selectedRow, 1);
                    String destination = (String) table.getValueAt(selectedRow, 2);
                    double price = (double) table.getValueAt(selectedRow, 3);
                    String startDate = (String) table.getValueAt(selectedRow, 4);
                    String endDate = (String) table.getValueAt(selectedRow, 5);
                    String description = (String) table.getValueAt(selectedRow, 6);
                    String status = (String) table.getValueAt(selectedRow, 7);
                    ImageIcon imageIcon = (ImageIcon) table.getValueAt(selectedRow, 8);  // Chỉnh sửa ở đây

                    // Lấy đường dẫn từ imageIcon (nếu cần)
                    String imagePath = imageIcon.getDescription();  // Lấy mô tả của ảnh (hoặc có thể dùng phương thức khác nếu cần)

                    // Tạo form chỉnh sửa với các tham số này
                    AddEditTourForm editForm = new AddEditTourForm(tourId, name, destination, price, startDate, endDate, description, status, imagePath, TourManagement.this);
                    editForm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn một tour để chỉnh sửa!");
                }
            }
        });


        // ActionListener cho nút Delete Tour
        btnDeleteTour.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int tourId = (int) table.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(null, "Bạn có muốn xoá tour này?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteTour(tourId);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn tour cần xoá!");
                }
            }
        });
    }

    // Load danh sách tour từ cơ sở dữ liệu
    public void loadTourData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT t.id_tour, t.tour_name, d.destination_name, t.price, t.start_date, t.end_date, t.description, t.status, t.image_url " +
                         "FROM tours t " +
                         "JOIN destinations d ON t.id_destination = d.id_destination";
            rs = stmt.executeQuery(sql);

            tableModel.setRowCount(0); // Clear previous data
            while (rs.next()) {
                int id = rs.getInt("id_tour");
                String name = rs.getString("tour_name");
                String destination = rs.getString("destination_name");
                double price = rs.getDouble("price");
                String startDate = rs.getString("start_date");
                String endDate = rs.getString("end_date");
                String description = rs.getString("description");
                String status = rs.getString("status");
                String imagePath = rs.getString("image_url");

                // Kiểm tra nếu không có ảnh thì hiển thị ảnh mặc định
                ImageIcon imageIcon = (imagePath != null && !imagePath.isEmpty()) ? new ImageIcon(imagePath) : new ImageIcon("default_image.jpg");

                // Thêm dữ liệu vào bảng
                tableModel.addRow(new Object[]{id, name, destination, price, startDate, endDate, description, status, imageIcon});
            }

            // Thiết lập renderer cho cột chứa hình ảnh
            table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                              boolean isSelected, boolean hasFocus,
                                                              int row, int column) {
                    if (value instanceof ImageIcon) {
                        ImageIcon icon = (ImageIcon) value;
                        Image image = icon.getImage();
                        // Điều chỉnh kích thước hình ảnh sao cho phù hợp với chiều cao hàng
                        Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        ImageIcon scaledIcon = new ImageIcon(scaledImage);

                        JLabel label = new JLabel(scaledIcon);
                        label.setHorizontalAlignment(JLabel.CENTER); // Căn giữa hình ảnh
                        return label;
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });

            // Thay đổi chiều rộng của cột hình ảnh
            table.getColumnModel().getColumn(8).setPreferredWidth(100); // Đặt chiều rộng cột chứa hình ảnh rộng hơn

            // Thay đổi chiều cao của các hàng
            table.setRowHeight(100); // Chiều cao của mỗi hàng, đủ lớn để hiển thị hình ảnh

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Xoá tour từ cơ sở dữ liệu
    private void deleteTour(int tourId) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM tours WHERE id_tour = ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, tourId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Xoá tour thành công!");
                loadTourData(); // Cập nhật lại danh sách tour
            } else {
                JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi xoá tour.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
