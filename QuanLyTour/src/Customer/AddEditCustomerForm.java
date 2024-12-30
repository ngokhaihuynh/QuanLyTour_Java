package Customer;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddEditCustomerForm extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtName;
    private JTextField txtAddress;
    private JTextField txtPhone;
    private JButton btnSave;
    private boolean isEdit = false;  // Biến này để xác định là thêm hay sửa
    private int customerId;  // Lưu ID khách hàng khi chỉnh sửa
    private CustomerManagement customerManagement; // Tham chiếu đến CustomerManagement

    // Constructor mặc định khi thêm khách hàng mới

    public AddEditCustomerForm(CustomerManagement customerManagement) {
        this.customerManagement = customerManagement;
        setLocationRelativeTo(null);
        setTitle("Thêm khách hàng");
        initUI(); // Khởi tạo giao diện mặc định
    }

 // Constructor mặc định
    public AddEditCustomerForm() {
        setTitle("Thêm khách hàng");
        initUI(); // Khởi tạo giao diện
        setLocationRelativeTo(null);
    }

    // Constructor khi chỉnh sửa khách hàng
 // Constructor khi chỉnh sửa khách hàng
    public AddEditCustomerForm(int customerId, String name, String address, String phone, CustomerManagement customerManagement) {
        this();  // Gọi constructor mặc định để thiết lập form
        setTitle("Sửa khách hàng");
        this.isEdit = true;  // Đặt biến isEdit là true để biết là chỉnh sửa
        this.customerId = customerId;
        this.customerManagement = customerManagement;  // Lưu tham chiếu CustomerManagement
        setLocationRelativeTo(null);
        // Điền thông tin khách hàng vào các trường
        txtName.setText(name);
        txtAddress.setText(address);
        txtPhone.setText(phone);
    }


    // Hàm để lưu thông tin khách hàng vào cơ sở dữ liệu
    private void saveCustomer() {
        String name = txtName.getText();
        String address = txtAddress.getText();
        String phone = txtPhone.getText();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = DatabaseConnection.getConnection();

            if (isEdit) { // Nếu là sửa thông tin khách hàng
                String sql = "UPDATE customers SET full_name = ?, address = ?, phone_number = ? WHERE id_customer = ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, address);
                pst.setString(3, phone);
                pst.setInt(4, customerId);
            } else { // Nếu là thêm khách hàng mới
                String sql = "INSERT INTO customers (full_name, address, phone_number) VALUES (?, ?, ?)";
                pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, address);
                pst.setString(3, phone);
            }

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Lưu thông tin khách hàng thành công!");
                if (customerManagement != null) {
                    customerManagement.loadCustomerData(); // Tải lại dữ liệu trong CustomerManagement
                }
                dispose(); // Đóng cửa sổ sau khi lưu thành công
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi lưu thông tin khách hàng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi lưu thông tin khách hàng.");
        } finally {
            try {
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Khởi tạo giao diện cho form
    public void initUI() {
        setTitle("Thêm khách hàng");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(4, 2, 10, 10)); // Sử dụng GridLayout để tổ chức các trường
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setLocationRelativeTo(null);
        setContentPane(contentPane);

        JLabel lblName = new JLabel("Tên khách hàng:");
        contentPane.add(lblName);

        txtName = new JTextField();
        contentPane.add(txtName);
        txtName.setColumns(10);

        JLabel lblAddress = new JLabel("Địa chỉ:");
        contentPane.add(lblAddress);

        txtAddress = new JTextField();
        contentPane.add(txtAddress);
        txtAddress.setColumns(10);

        JLabel lblPhone = new JLabel("Số điện thoại:");
        contentPane.add(lblPhone);

        txtPhone = new JTextField();
        contentPane.add(txtPhone);
        txtPhone.setColumns(10);

        btnSave = new JButton("Lưu");
        contentPane.add(btnSave);

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveCustomer();
            }
        });
    }
}
