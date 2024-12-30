package Destinations;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DestinationManagement extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;

    public DestinationManagement() {
        setLayout(new BorderLayout());

        // Tạo các cột cho JTable (Cập nhật thêm cột "Mô tả")
        String[] columnNames = {"ID", "Tên địa điểm", "Hình ảnh", "Ngày tạo", "Mô tả"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddDestination = new JButton("Thêm");
        JButton btnEditDestination = new JButton("Sửa");
        JButton btnDeleteDestination = new JButton("Xoá");

        buttonPanel.add(btnAddDestination);
        buttonPanel.add(btnEditDestination);
        buttonPanel.add(btnDeleteDestination);
     // Định nghĩa nút "Thêm"
        btnAddDestination.setBackground(new Color(50, 205, 50)); // Màu xanh lá cây
        btnAddDestination.setForeground(Color.WHITE);           // Chữ trắng
        btnAddDestination.setFont(new Font("Arial", Font.BOLD, 14));

        // Định nghĩa nút "Sửa"
        btnEditDestination.setBackground(new Color(30, 144, 255)); // Màu xanh dương
        btnEditDestination.setForeground(Color.WHITE);            // Chữ trắng
        btnEditDestination.setFont(new Font("Arial", Font.BOLD, 14));

        // Định nghĩa nút "Xoá"
        btnDeleteDestination.setBackground(new Color(220, 20, 60)); // Màu đỏ
        btnDeleteDestination.setForeground(Color.WHITE);           // Chữ trắng
        btnDeleteDestination.setFont(new Font("Arial", Font.BOLD, 14));


     // Thêm tiêu đề phía trên bảng
        JLabel lblNewLabel = new JLabel("Danh sách địa điểm", JLabel.CENTER);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        add(lblNewLabel, BorderLayout.NORTH);
        lblNewLabel.setForeground(new Color(34, 139, 34)); // Màu xanh lá (mã màu RGB: 34, 139, 34)


        // Thêm các nút vào vùng SOUTH
        add(buttonPanel, BorderLayout.SOUTH);

        // Load danh sách địa điểm từ cơ sở dữ liệu
        loadDestinationData();

        // ActionListener cho nút Add Destination
        btnAddDestination.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Tạo form thêm địa điểm mới và hiển thị nó
                AddEditDestinationForm form = new AddEditDestinationForm(DestinationManagement.this);
                form.setVisible(true); // Đặt form hiển thị
            }
        });

        // ActionListener cho nút Edit Destination
        btnEditDestination.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Lấy ID, Tên, Hình ảnh, Quốc gia và Mô tả từ bảng
                    int destinationId = (int) table.getValueAt(selectedRow, 0);
                    String name = (String) table.getValueAt(selectedRow, 1);
                    ImageIcon imageIcon = (ImageIcon) table.getValueAt(selectedRow, 2); // Lấy ImageIcon từ bảng
                    String imagePath = imageIcon != null ? imageIcon.getDescription() : null; // Nếu có, lấy đường dẫn ảnh
                    String country = (String) table.getValueAt(selectedRow, 3);
                    String description = (String) table.getValueAt(selectedRow, 4);

                    // Kiểm tra và đảm bảo không có trường nào bị null
                    if (name != null && imagePath != null && country != null && description != null) {
                        // Tạo form chỉnh sửa với các tham số này
                        AddEditDestinationForm editForm = new AddEditDestinationForm(destinationId, name, imagePath, country, description, DestinationManagement.this);
                        editForm.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Thông tin địa điểm không đầy đủ!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn địa điểm để chỉnh sửa!");
                }
            }
        });

        // ActionListener cho nút Delete Destination
        btnDeleteDestination.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int destinationId = (int) table.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(null, "Bạn có muốn xoá địa điểm?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteDestination(destinationId);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn địa điểm cần xoá!");
                }
            }
        });
    }

    // Load danh sách địa điểm từ cơ sở dữ liệu
    public void loadDestinationData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT id_destination, destination_name, image, country, description FROM destinations";
            rs = stmt.executeQuery(sql);

            tableModel.setRowCount(0); // Clear previous data
            while (rs.next()) {
                int id = rs.getInt("id_destination");
                String name = rs.getString("destination_name");
                String imagePath = rs.getString("image"); // Đường dẫn hình ảnh
                String country = rs.getString("country");
                String description = rs.getString("description");

                // Tạo đối tượng ImageIcon từ đường dẫn hình ảnh
                ImageIcon imageIcon = new ImageIcon(imagePath); 

                // Thêm dữ liệu vào bảng, bao gồm cả ImageIcon
                tableModel.addRow(new Object[]{id, name, imageIcon, country, description});
            }

            // Thiết lập renderer cho cột chứa hình ảnh
            table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
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
            table.getColumnModel().getColumn(2).setPreferredWidth(100); // Đặt chiều rộng cột chứa hình ảnh rộng hơn

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

    // Xoá địa điểm từ cơ sở dữ liệu
    private void deleteDestination(int destinationId) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM destinations WHERE id_destination = ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, destinationId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Xoá địa điểm thành công!");
                loadDestinationData(); // Cập nhật lại danh sách địa điểm
            } else {
                JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi xoá địa điểm.");
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
