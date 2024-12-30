package Tour;

import database.DatabaseConnection;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.border.EmptyBorder;

public class AddEditTourForm extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField txtTourName;
    private JTextField txtPrice;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JTextArea txtDescription;
    private JComboBox<String> cmbDestination;
    private JComboBox<String> cmbStatus;
    private JLabel lblImage;
    private String imagePath;
    private int tourId;

    private TourManagement tourManagementPanel;

    // Constructor khi thêm tour mới
    public AddEditTourForm(TourManagement tourManagementPanel) {
        this.tourManagementPanel = tourManagementPanel;
        setTitle("Thêm Tour");

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        panel.setLayout(new GridLayout(10, 2, 10, 10));
        setLocationRelativeTo(null);
        setContentPane(panel);
        setSize(600, 700);  // Đã tăng kích thước cửa sổ
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents(panel);
        loadDestinations();
    }

    // Constructor khi chỉnh sửa tour hiện tại
    public AddEditTourForm(int tourId, String name, String destination, double price, String startDate, String endDate, 
                           String description, String status, String imagePath, TourManagement tourManagementPanel) {
        this(tourManagementPanel);
        this.tourId = tourId;

        // Điền sẵn các thông tin hiện tại vào các trường trong form
        txtTourName.setText(name);
        txtPrice.setText(String.valueOf(price));
        
        // Chuyển đổi chuỗi ngày thành đối tượng Date để hiển thị trên JDateChooser
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            startDateChooser.setDate(sdf.parse(startDate));
            endDateChooser.setDate(sdf.parse(endDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        txtDescription.setText(description);
        cmbStatus.setSelectedItem(status);
        this.imagePath = imagePath;
        lblImage.setIcon(new ImageIcon(imagePath));
    }

    private void initComponents(JPanel panel) {
        // Tạo các trường thông tin cho tour
        JLabel lblTourName = new JLabel("Tên Tour:");
        txtTourName = new JTextField();
        JLabel lblDestination = new JLabel("Điểm đến:");
        cmbDestination = new JComboBox<>();
        JLabel lblPrice = new JLabel("Giá:");
        txtPrice = new JTextField();
        JLabel lblStartDate = new JLabel("Ngày bắt đầu:");
        startDateChooser = new JDateChooser();
        JLabel lblEndDate = new JLabel("Ngày kết thúc:");
        endDateChooser = new JDateChooser();
        JLabel lblDescription = new JLabel("Mô tả:");
        txtDescription = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        JLabel lblStatus = new JLabel("Trạng thái:");
        cmbStatus = new JComboBox<>(new String[]{"Trống", "Đang diễn ra", "Hoàn thành", "Đã huỷ"});
        JLabel lblImageLabel = new JLabel("Hình ảnh:");
        lblImage = new JLabel();

        // Thêm các trường vào panel
        panel.add(lblTourName);
        panel.add(txtTourName);
        panel.add(lblDestination);
        panel.add(cmbDestination);
        panel.add(lblPrice);
        panel.add(txtPrice);
        panel.add(lblStartDate);
        panel.add(startDateChooser);
        panel.add(lblEndDate);
        panel.add(endDateChooser);
        panel.add(lblDescription);
        panel.add(scrollPane);
        panel.add(lblStatus);
        panel.add(cmbStatus);
        panel.add(lblImageLabel);
        panel.add(lblImage);

        // Thêm nút "Chọn hình ảnh"
        JButton btnChooseImage = new JButton("Chọn hình ảnh");
        panel.add(btnChooseImage);
        JButton btnSave = new JButton("Lưu");
        panel.add(btnSave);
        JButton btnCancel = new JButton("Hủy");
        panel.add(btnCancel);

        // Lắng nghe sự kiện chọn hình ảnh
        btnChooseImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn Hình Ảnh");
                int result = fileChooser.showOpenDialog(AddEditTourForm.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    imagePath = fileChooser.getSelectedFile().getAbsolutePath();
                    lblImage.setIcon(new ImageIcon(imagePath)); // Hiển thị ảnh sau khi chọn
                }
            }
        });

        // Lắng nghe sự kiện lưu thông tin
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = txtTourName.getText();
                String destination = (String) cmbDestination.getSelectedItem();
                double price = Double.parseDouble(txtPrice.getText());

                // Kiểm tra ngày
                if (startDateChooser.getDate() == null || endDateChooser.getDate() == null) {
                    JOptionPane.showMessageDialog(AddEditTourForm.this, "Vui lòng chọn ngày bắt đầu và ngày kết thúc.");
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String startDate = sdf.format(startDateChooser.getDate());
                String endDate = sdf.format(endDateChooser.getDate());
                String description = txtDescription.getText();
                String status = (String) cmbStatus.getSelectedItem();

                if (tourId == 0) {
                    addTour(name, destination, price, startDate, endDate, description, status);
                } else {
                    editTour(name, destination, price, startDate, endDate, description, status);
                }
            }
        });

        // Lắng nghe sự kiện hủy bỏ
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Đóng cửa sổ
            }
        });
    }

    // Hàm tải danh sách điểm đến từ cơ sở dữ liệu
    private void loadDestinations() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT destination_name FROM destinations";
            rs = stmt.executeQuery(sql);

            // Thêm các điểm đến vào combobox
            while (rs.next()) {
                cmbDestination.addItem(rs.getString("destination_name"));
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

    // Thêm tour vào cơ sở dữ liệu
    private void addTour(String name, String destination, double price, String startDate, String endDate, String description, String status) {
        if (imagePath == null || imagePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hình ảnh.");
            return;
        }

        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Lấy id của điểm đến từ bảng destinations
            String getDestinationIdSql = "SELECT id_destination FROM destinations WHERE destination_name = ?";
            pst = conn.prepareStatement(getDestinationIdSql);
            pst.setString(1, destination);
            ResultSet rs = pst.executeQuery();
            int destinationId = 0;
            if (rs.next()) {
                destinationId = rs.getInt("id_destination");
            }

            String sql = "INSERT INTO tours (tour_name, id_destination, price, start_date, end_date, description, status, image_url) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setInt(2, destinationId);
            pst.setDouble(3, price);
            pst.setString(4, startDate);
            pst.setString(5, endDate);
            pst.setString(6, description);
            pst.setString(7, status);
            pst.setString(8, imagePath);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Thêm tour thành công!");
                tourManagementPanel.loadTourData(); // Cập nhật lại danh sách tour
                dispose(); // Đóng cửa sổ
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

    // Chỉnh sửa tour trong cơ sở dữ liệu
    private void editTour(String name, String destination, double price, String startDate, String endDate, String description, String status) {
        if (imagePath == null || imagePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hình ảnh.");
            return;
        }

        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Lấy id của điểm đến từ bảng destinations
            String getDestinationIdSql = "SELECT id_destination FROM destinations WHERE destination_name = ?";
            pst = conn.prepareStatement(getDestinationIdSql);
            pst.setString(1, destination);
            ResultSet rs = pst.executeQuery();
            int destinationId = 0;
            if (rs.next()) {
                destinationId = rs.getInt("id_destination");
            }

            String sql = "UPDATE tours SET tour_name = ?, id_destination = ?, price = ?, start_date = ?, end_date = ?, description = ?, status = ?, image_url = ? WHERE id_tour = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setInt(2, destinationId);
            pst.setDouble(3, price);
            pst.setString(4, startDate);
            pst.setString(5, endDate);
            pst.setString(6, description);
            pst.setString(7, status);
            pst.setString(8, imagePath);
            pst.setInt(9, tourId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật tour thành công!");
                tourManagementPanel.loadTourData(); // Cập nhật lại danh sách tour
                dispose(); // Đóng cửa sổ
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
