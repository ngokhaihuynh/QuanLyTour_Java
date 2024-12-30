package Destinations;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import database.DatabaseConnection; // Giả sử bạn đã có lớp này để kết nối cơ sở dữ liệu

public class AddEditDestinationForm extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTextField txtName, txtImage, txtCountry;
    private JTextArea txtDescription;
    private JButton btnSave, btnCancel, btnBrowseImage;
    private int destinationId;
    private DestinationManagement parent;

    // Constructor cho thêm mới
    /**
     * @wbp.parser.constructor
     */
    public AddEditDestinationForm(DestinationManagement parent) {
        this.parent = parent;
        this.destinationId = -1;
        initUI();
    }

    // Constructor cho chỉnh sửa
    public AddEditDestinationForm(int id, String name, String image, String country, String description, DestinationManagement parent) {
        this.parent = parent;
        this.destinationId = id;
        initUI();
        txtName.setText(name);
        txtImage.setText(image);
        txtCountry.setText(country);
        txtDescription.setText(description);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        setTitle(destinationId == -1 ? "Thêm địa điểm" : "Chỉnh sửa địa điểm");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 350);
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Tạo các trường nhập liệu và nhãn
        JLabel lblName = new JLabel("Tên địa điểm:");
        getContentPane().add(lblName, gbc);

        txtName = new JTextField(20);
        gbc.gridx = 1;
        getContentPane().add(txtName, gbc);

        JLabel lblCountry = new JLabel("Quốc gia:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        getContentPane().add(lblCountry, gbc);

        txtCountry = new JTextField(20);
        gbc.gridx = 1;
        getContentPane().add(txtCountry, gbc);

        JLabel lblImage = new JLabel("Hình ảnh:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        getContentPane().add(lblImage, gbc);

        txtImage = new JTextField(20);
        gbc.gridx = 1;
        getContentPane().add(txtImage, gbc);

        btnBrowseImage = new JButton("Chọn ảnh");
        gbc.gridx = 2;
        getContentPane().add(btnBrowseImage, gbc);

        JLabel lblDescription = new JLabel("Mô tả:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        getContentPane().add(lblDescription, gbc);

        txtDescription = new JTextArea(4, 20);
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        getContentPane().add(scrollPane, gbc);

        // Nút Lưu
        btnSave = new JButton(destinationId == -1 ? "Lưu" : "Cập nhật");
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        getContentPane().add(btnSave, gbc);

        // Nút Hủy
        btnCancel = new JButton("Hủy");
        gbc.gridx = 2;
        getContentPane().add(btnCancel, gbc);

        // Hành động cho nút "Chọn ảnh"
        btnBrowseImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseImage();
            }
        });

        // Lưu thông tin địa điểm
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveDestination();
            }
        });

        // Hủy và đóng cửa sổ
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null); // Hiển thị form ở trung tâm màn hình
        setModal(true);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn tệp ảnh");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Lọc chỉ cho phép chọn tệp ảnh
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Hình ảnh (JPG, PNG, GIF)", "jpg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtImage.setText(selectedFile.getAbsolutePath());
        }
    }

    private void saveDestination() {
        String name = txtName.getText().trim();
        String image = txtImage.getText().trim();
        String country = txtCountry.getText().trim();
        String description = txtDescription.getText().trim();

        if (name.isEmpty() || image.isEmpty() || country.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (destinationId == -1) { // Thêm mới
                String sql = "INSERT INTO destinations (destination_name, country, image, description) VALUES (?, ?, ?, ?)";
                pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, country);
                pst.setString(3, image);
                pst.setString(4, description);
            } else { // Cập nhật
                String sql = "UPDATE destinations SET destination_name = ?, country = ?, image = ?, description = ? WHERE id_destination = ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, country);
                pst.setString(3, image);
                pst.setString(4, description);
                pst.setInt(5, destinationId);
            }

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, destinationId == -1 ? "Lưu thông tin thành công!" : "Cập nhật thông tin thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                parent.loadDestinationData(); // Reload danh sách
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
