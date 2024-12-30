package Customer;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CustomerManagement extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;

    public CustomerManagement() {
        setLayout(new BorderLayout());

        // Tạo các cột cho JTable
        String[] columnNames = {"ID", "Tên khách hàng", "Địa chỉ", "Số điện thoại", "Ngày tạo"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
        JButton btnAddCustomer = new JButton("Thêm");
        JButton btnEditCustomer = new JButton("Sửa");
        JButton btnDeleteCustomer = new JButton("Xoá");
     // Định nghĩa nút "Thêm"
        btnAddCustomer.setBackground(new Color(50, 205, 50)); // Màu xanh lá cây
        btnAddCustomer.setForeground(Color.WHITE);           // Chữ trắng
        btnAddCustomer.setFont(new Font("Arial", Font.BOLD, 14));

        // Định nghĩa nút "Sửa"
        btnEditCustomer.setBackground(new Color(30, 144, 255)); // Màu xanh dương
        btnEditCustomer.setForeground(Color.WHITE);            // Chữ trắng
        btnEditCustomer.setFont(new Font("Arial", Font.BOLD, 14));

        // Định nghĩa nút "Xoá"
        btnDeleteCustomer.setBackground(new Color(220, 20, 60)); // Màu đỏ
        btnDeleteCustomer.setForeground(Color.WHITE);           // Chữ trắng
        btnDeleteCustomer.setFont(new Font("Arial", Font.BOLD, 14));


        buttonPanel.add(btnAddCustomer);
        buttonPanel.add(btnEditCustomer);
        buttonPanel.add(btnDeleteCustomer);

        // Thêm tiêu đề phía trên bảng
        JLabel lblNewLabel = new JLabel("Danh sách khách hàng", JLabel.CENTER);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18)); // Đổi font thành đậm
        lblNewLabel.setForeground(new Color(34, 139, 34)); // Màu xanh lá (mã màu RGB: 34, 139, 34)
        add(lblNewLabel, BorderLayout.NORTH);


        // Thêm các nút vào vùng SOUTH
        add(buttonPanel, BorderLayout.SOUTH);

        // Load danh sách khách hàng từ cơ sở dữ liệu
        loadCustomerData();

        // ActionListener cho nút Add Customer
     // Thêm ActionListener cho nút Add Customer
        btnAddCustomer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Tạo form thêm khách hàng mới
                AddEditCustomerForm form = new AddEditCustomerForm(CustomerManagement.this);
                form.initUI();  // Gọi method để khởi tạo giao diện
                form.setVisible(true);
            }
        });


        
     // ActionListener cho nút Edit Customer
        btnEditCustomer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int customerId = (int) table.getValueAt(selectedRow, 0);
                    String name = (String) table.getValueAt(selectedRow, 1);
                    String address = (String) table.getValueAt(selectedRow, 2);
                    String phone = (String) table.getValueAt(selectedRow, 3);

                    // Gọi constructor chỉnh sửa với thông tin khách hàng
                    AddEditCustomerForm editForm = new AddEditCustomerForm(customerId, name, address, phone, CustomerManagement.this);
                    editForm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn khách hàng để chỉnh sửa!!");
                }
            }
        });


        // ActionListener cho nút Delete Customer
        btnDeleteCustomer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int customerId = (int) table.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(null, "Bạn có muốn xoá khách hàng?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteCustomer(customerId);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn khách hàng cần xoá!!");
                }
            }
        });
    }

    // Load danh sách khách hàng từ cơ sở dữ liệu
    public void loadCustomerData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM customers";
            rs = stmt.executeQuery(sql);

            tableModel.setRowCount(0);
            while (rs.next()) {
                int id = rs.getInt("id_customer");  // Sửa tên cột cho đúng với bảng customers
                String name = rs.getString("full_name");  // Tên khách hàng
                String address = rs.getString("address");  // Địa chỉ khách hàng
                String phone = rs.getString("phone_number");  // Số điện thoại khách hàng
                String created = rs.getString("created_at");
                tableModel.addRow(new Object[]{id, name, address, phone, created});
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

    // Xoá khách hàng từ cơ sở dữ liệu
    private void deleteCustomer(int customerId) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM customers WHERE id_customer = ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, customerId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Xoá khách hàng thành công!");
                loadCustomerData(); // Cập nhật lại danh sách khách hàng
            } else {
                JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi xoá khách hàng.");
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
