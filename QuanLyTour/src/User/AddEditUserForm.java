package User;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddEditUserForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private int userId;
    private UserManagement parentForm; // Đối tượng UserManagement

    // Constructor để chỉnh sửa người dùng
    public AddEditUserForm(int userId, String username, String email, String role, UserManagement parentForm) {
        this(parentForm);  // Gọi constructor mặc định
        this.parentForm = parentForm;  // Lưu đối tượng UserManagement
        txtUsername.setText(username);
        txtEmail.setText(email);
        cmbRole.setSelectedItem(role);
        this.userId = userId;
        setLocationRelativeTo(null);
    }

    // Constructor mặc định khi thêm người dùng mới
    public AddEditUserForm(UserManagement parentForm) {
        setTitle("Thêm/Sửa Tài khoản");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 400);
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);
        setLocationRelativeTo(null);

        // Các thành phần giao diện
        JLabel lblUsername = new JLabel("Tên đăng nhập");
        lblUsername.setBounds(50, 50, 120, 25);
        contentPane.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(200, 50, 180, 25);
        contentPane.add(txtUsername);

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setBounds(50, 100, 120, 25);
        contentPane.add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(200, 100, 180, 25);
        contentPane.add(txtEmail);

        JLabel lblPassword = new JLabel("Mật khẩu");
        lblPassword.setBounds(50, 150, 120, 25);
        contentPane.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(200, 150, 180, 25);
        contentPane.add(txtPassword);

        JLabel lblRole = new JLabel("Vai trò");
        lblRole.setBounds(50, 200, 120, 25);
        contentPane.add(lblRole);

        cmbRole = new JComboBox<>(new String[]{"Admin", "User"});
        cmbRole.setBounds(200, 200, 180, 25);
        contentPane.add(cmbRole);

        JButton btnSave = new JButton("Lưu");
        btnSave.setBounds(150, 270, 120, 30);
        contentPane.add(btnSave);

        // Action listener cho nút Save
     // Action listener cho nút Save
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Lấy dữ liệu người dùng nhập vào
                String username = txtUsername.getText();
                String email = txtEmail.getText();
                String password = new String(txtPassword.getPassword());
                String role = (String) cmbRole.getSelectedItem();

                // Kiểm tra các trường bắt buộc
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.");
                    return;
                }

                // Xử lý thêm hoặc sửa người dùng
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql;
                    PreparedStatement pst;

                    if (userId != 0) {
                        // Cập nhật người dùng
                        sql = "UPDATE users SET username = ?, email = ?, password = ?, role = ? WHERE id_user = ?";
                        pst = conn.prepareStatement(sql);
                        pst.setInt(5, userId);  // Set ID người dùng cho update
                    } else {
                        // Thêm người dùng mới
                        sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
                        pst = conn.prepareStatement(sql);
                    }

                    pst.setString(1, username);
                    pst.setString(2, email);
                    pst.setString(3, password);
                    pst.setString(4, role);

                    // Thực thi câu lệnh SQL
                    int rowsAffected = pst.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, userId == 0 ? "Người dùng đã được thêm thành công!" : "Người dùng đã được cập nhật thành công!");
                        if (parentForm != null) {
                            parentForm.loadUserData();  // Gọi lại phương thức load dữ liệu sau khi sửa
                        }
                        dispose();  // Đóng form
                    } else {
                        JOptionPane.showMessageDialog(null, "Lỗi khi lưu người dùng. Vui lòng thử lại.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi lưu người dùng.");
                }
            }
        });

    }
}
