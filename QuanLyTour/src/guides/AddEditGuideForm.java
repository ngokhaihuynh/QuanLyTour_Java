package guides;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddEditGuideForm extends JFrame {

    private JTextField txtFullName, txtPhoneNumber, txtLanguage;
    private JComboBox<String> cbStatus;  // ComboBox cho trạng thái
    private JButton btnSave, btnCancel;
    private int guideId = -1;

    public AddEditGuideForm(int guideId, String fullName, String phoneNumber, String language, String status) {
        this.guideId = guideId;
        setLocationRelativeTo(null);
        setTitle(guideId == -1 ? "Thêm Hướng Dẫn Viên" : "Sửa Hướng Dẫn Viên");
        setSize(500,600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel chính
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tên trường
        JLabel lblFullName = new JLabel("Họ và tên:");
        lblFullName.setFont(new Font("Arial", Font.BOLD, 14));
        txtFullName = new JTextField();
        txtFullName.setPreferredSize(new Dimension(300, 30));
        txtFullName.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblPhoneNumber = new JLabel("Số điện thoại:");
        lblPhoneNumber.setFont(new Font("Arial", Font.BOLD, 14));
        txtPhoneNumber = new JTextField();
        txtPhoneNumber.setPreferredSize(new Dimension(300, 30));
        txtPhoneNumber.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblLanguage = new JLabel("Ngôn ngữ:");
        lblLanguage.setFont(new Font("Arial", Font.BOLD, 14));
        txtLanguage = new JTextField();
        txtLanguage.setPreferredSize(new Dimension(300, 30));
        txtLanguage.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));

        // ComboBox trạng thái
        cbStatus = new JComboBox<>(new String[]{"Trống", "Đang làm việc", "Nghỉ việc"});
        cbStatus.setFont(new Font("Arial", Font.PLAIN, 14));
        cbStatus.setPreferredSize(new Dimension(300, 30));

        // Thêm các trường vào panel
        panel.add(lblFullName);
        panel.add(Box.createVerticalStrut(10));
        panel.add(txtFullName);
        panel.add(Box.createVerticalStrut(15));

        panel.add(lblPhoneNumber);
        panel.add(Box.createVerticalStrut(10));
        panel.add(txtPhoneNumber);
        panel.add(Box.createVerticalStrut(15));

        panel.add(lblLanguage);
        panel.add(Box.createVerticalStrut(10));
        panel.add(txtLanguage);
        panel.add(Box.createVerticalStrut(15));

        panel.add(lblStatus);
        panel.add(Box.createVerticalStrut(10));
        panel.add(cbStatus);
        panel.add(Box.createVerticalStrut(20));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        btnSave = new JButton(guideId == -1 ? "Thêm" : "Lưu thay đổi");
        btnSave.setFont(new Font("Arial", Font.BOLD, 14));
        btnSave.setBackground(new Color(30, 144, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(120, 40));

        btnCancel = new JButton("Huỷ");
        btnCancel.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancel.setBackground(new Color(220, 20, 60));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(btnSave);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(btnCancel);

        // Thêm buttonPanel vào form
        panel.add(buttonPanel);

        // Add panel chính vào cửa sổ
        add(panel);

        // Điền dữ liệu khi sửa
        if (guideId != -1) {
            txtFullName.setText(fullName);
            txtPhoneNumber.setText(phoneNumber);
            txtLanguage.setText(language);
            cbStatus.setSelectedItem(status);
        }

        // Xử lý sự kiện nút Save
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGuide();
            }
        });

        // Xử lý sự kiện nút Cancel
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void saveGuide() {
        String fullName = txtFullName.getText();
        String phoneNumber = txtPhoneNumber.getText();
        String language = txtLanguage.getText();
        String status = (String) cbStatus.getSelectedItem();

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pst;

            if (guideId == -1) { // Thêm mới hướng dẫn viên
                String sql = "INSERT INTO guides (full_name, phone_number, language, status) VALUES (?, ?, ?, ?)";
                pst = conn.prepareStatement(sql);
                pst.setString(1, fullName);
                pst.setString(2, phoneNumber);
                pst.setString(3, language);
                pst.setString(4, status);
            } else { // Cập nhật hướng dẫn viên
                String sql = "UPDATE guides SET full_name = ?, phone_number = ?, language = ?, status = ? WHERE id_guide = ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, fullName);
                pst.setString(2, phoneNumber);
                pst.setString(3, language);
                pst.setString(4, status);
                pst.setInt(5, guideId);
            }

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, guideId == -1 ? "Thêm thành công !" : "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Đóng form sau khi lưu
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi lưu.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

            pst.close();
            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving guide: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
