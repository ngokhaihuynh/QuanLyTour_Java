package User;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserManagement extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;

    public UserManagement() {
        setLayout(new BorderLayout()); // Sử dụng BorderLayout để giao diện linh hoạt hơn
        
        // Tạo các cột cho JTable
        String[] columnNames = {"ID", "Tên đăng nhập", "Email", "Vai trò", "Ngày tạo"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Thêm bảng vào vùng CENTER của BorderLayout
        add(scrollPane, BorderLayout.CENTER);

        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Tạo panel chứa các nút
        JButton btnAddUser = new JButton("Thêm");
        JButton btnEditUser = new JButton("Sửa");
        JButton btnDeleteUser = new JButton("Xoá");
        // Định nghĩa nút "Thêm"
        btnAddUser.setBackground(new Color(50, 205, 50)); // Màu xanh lá cây
        btnAddUser.setForeground(Color.WHITE);           // Chữ trắng
        btnAddUser.setFont(new Font("Arial", Font.BOLD, 14));

        // Định nghĩa nút "Sửa"
        btnEditUser.setBackground(new Color(30, 144, 255)); // Màu xanh dương
        btnEditUser.setForeground(Color.WHITE);            // Chữ trắng
        btnEditUser.setFont(new Font("Arial", Font.BOLD, 14));

        // Định nghĩa nút "Xoá"
        btnDeleteUser.setBackground(new Color(220, 20, 60)); // Màu đỏ
        btnDeleteUser.setForeground(Color.WHITE);           // Chữ trắng
        btnDeleteUser.setFont(new Font("Arial", Font.BOLD, 14));

        buttonPanel.add(btnAddUser);
        buttonPanel.add(btnEditUser);
        buttonPanel.add(btnDeleteUser);

        // Thêm tiêu đề phía trên bảng
        JLabel lblNewLabel = new JLabel("Danh sách tài khoản", JLabel.CENTER);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        add(lblNewLabel, BorderLayout.NORTH); // Thêm tiêu đề vào vùng NORTH
        lblNewLabel.setForeground(new Color(34, 139, 34)); // Màu xanh lá (mã màu RGB: 34, 139, 34)
        // Thêm các nút vào vùng SOUTH
        add(buttonPanel, BorderLayout.SOUTH);

        // Load danh sách người dùng từ cơ sở dữ liệu
        loadUserData();

        // ActionListener cho nút Add User
        btnAddUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddEditUserForm addForm = new AddEditUserForm(UserManagement.this);
                addForm.setVisible(true);
            }
        });

        // ActionListener cho nút Edit User
        btnEditUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int userId = (int) table.getValueAt(selectedRow, 0);
                    String username = (String) table.getValueAt(selectedRow, 1);
                    String email = (String) table.getValueAt(selectedRow, 2);
                    String role = (String) table.getValueAt(selectedRow, 3);

                    AddEditUserForm editForm = new AddEditUserForm(userId, username, email, role, UserManagement.this);
                    editForm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn tài khoản để chỉnh sửa!!");
                }
            }
        });

        // ActionListener cho nút Delete User
        btnDeleteUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int userId = (int) table.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(null, "Bạn có muốn xoá tài khoản?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteUser(userId);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn tài khoản cần xoá!!");
                }
            }
        });
    }

    public void loadUserData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM users";
            rs = stmt.executeQuery(sql);

            tableModel.setRowCount(0);
            while (rs.next()) {
                int id = rs.getInt("id_user");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String role = rs.getString("role");
                String created = rs.getString("created_at");

                tableModel.addRow(new Object[]{id, username, email, role, created});
            }

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

    private void deleteUser(int userId) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM users WHERE id_user = ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Xoá tài khoản thành công!");
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(null, "Error deleting user.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while deleting the user.");
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
