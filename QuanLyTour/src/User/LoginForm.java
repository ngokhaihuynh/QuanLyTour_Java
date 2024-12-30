package User;

import Home.MainForm;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DatabaseConnection;

public class LoginForm extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginForm frame = new LoginForm();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public LoginForm() {
        // Thiết lập frame
        setTitle("Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 350);
        setResizable(false);
        setLocationRelativeTo(null);
        

        // Panel chính (chia làm 2 phần: logo và đăng nhập)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        // Panel bên trái (logo)
        JPanel leftPanel = new JPanel();
        leftPanel.setBounds(0, 0, 250, 350);
        leftPanel.setBackground(new Color(30, 144, 255)); // Màu xanh dương
        leftPanel.setLayout(new BorderLayout());
        mainPanel.add(leftPanel);

        // Thêm logo vào panel bên trái
        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setIcon(new ImageIcon("D:\\QuanLyTour\\QuanLyTour\\QuanLyTour\\src\\img\\anhdulich.jpg")); // Thay đường dẫn logo
        leftPanel.add(lblLogo, BorderLayout.CENTER);

        // Panel bên phải (form đăng nhập)
        JPanel rightPanel = new JPanel();
        rightPanel.setBounds(250, 0, 350, 350);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(null);
        mainPanel.add(rightPanel);

        // Tiêu đề
        JLabel lblTitle = new JLabel("Đăng Nhập");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setBounds(120, 20, 150, 30);
        lblTitle.setForeground(new Color(30, 144, 255)); // Màu xanh
        rightPanel.add(lblTitle);

        // Username label và text field
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        lblUsername.setBounds(30, 80, 100, 30);
        rightPanel.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(130, 80, 180, 30);
        txtUsername.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255)));
        rightPanel.add(txtUsername);

        // Password label và text field
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPassword.setBounds(30, 130, 100, 30);
        rightPanel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 130, 180, 30);
        txtPassword.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255)));
        rightPanel.add(txtPassword);

        // Nút "Login"
        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(130, 200, 80, 30);
        btnLogin.setBackground(new Color(30, 144, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        rightPanel.add(btnLogin);

        // Nút "Cancel"
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(230, 200, 80, 30);
        btnCancel.setBackground(new Color(220, 20, 60));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        rightPanel.add(btnCancel);

        // Xử lý sự kiện nút "Login"
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());

                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, username);
                    pst.setString(2, password);

                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, " Đăng nhập thành công !");
                        dispose();

                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                MainForm mainForm = new MainForm();
                                mainForm.setVisible(true);
                            }
                        });
                    } else {
                        JOptionPane.showMessageDialog(null, "ên người dùng hoặc mật khẩu không hợp lệ.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi kết nối đến cơ sở dữ liệu.");
                }
            }
        });

        // Xử lý sự kiện nút "Cancel"
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
