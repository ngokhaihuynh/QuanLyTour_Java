package guides;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GuideManagement extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public GuideManagement() {
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Tên hướng dẫn viên", "Số điện thoại", "Ngôn ngữ", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddGuide = new JButton("Thêm");
        JButton btnEditGuide = new JButton("Sửa");
        JButton btnDeleteGuide = new JButton("Xoá");

        buttonPanel.add(btnAddGuide);
        buttonPanel.add(btnEditGuide);
        buttonPanel.add(btnDeleteGuide);
     // Định nghĩa nút "Thêm"
        btnAddGuide.setBackground(new Color(50, 205, 50)); // Màu xanh lá cây
        btnAddGuide.setForeground(Color.WHITE);           // Chữ trắng
        btnAddGuide.setFont(new Font("Arial", Font.BOLD, 14));

        // Định nghĩa nút "Sửa"
        btnEditGuide.setBackground(new Color(30, 144, 255)); // Màu xanh dương
        btnEditGuide.setForeground(Color.WHITE);            // Chữ trắng
        btnEditGuide.setFont(new Font("Arial", Font.BOLD, 14));

        // Định nghĩa nút "Xoá"
        btnDeleteGuide.setBackground(new Color(220, 20, 60)); // Màu đỏ
        btnDeleteGuide.setForeground(Color.WHITE);           // Chữ trắng
        btnDeleteGuide.setFont(new Font("Arial", Font.BOLD, 14));





        JLabel lblTitle = new JLabel("Quản lý hướng dẫn viên", JLabel.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18)); // Đổi font thành đậm
        lblTitle.setForeground(new Color(34, 139, 34)); // Màu xanh lá (mã màu RGB: 34, 139, 34)
        add(lblTitle, BorderLayout.NORTH);

        add(buttonPanel, BorderLayout.SOUTH);

        loadGuideData();

        btnAddGuide.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddEditGuideForm form = new AddEditGuideForm(-1, "", "", "", "Trống");
                form.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        loadGuideData();
                    }
                });
                form.setVisible(true);
            }
        });

        btnEditGuide.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int guideId = (int) table.getValueAt(selectedRow, 0);
                    String name = (String) table.getValueAt(selectedRow, 1);
                    String phone = (String) table.getValueAt(selectedRow, 2);
                    String language = (String) table.getValueAt(selectedRow, 3);
                    String status = (String) table.getValueAt(selectedRow, 4);

                    AddEditGuideForm editForm = new AddEditGuideForm(guideId, name, phone, language, status);
                    editForm.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                            loadGuideData();
                        }
                    });
                    editForm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn hướng dẫn viên để chỉnh sửa!");
                }
            }
        });

        btnDeleteGuide.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int guideId = (int) table.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(null, "Bạn có muốn xoá hướng dẫn viên?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteGuide(guideId);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn hướng dẫn viên cần xoá!");
                }
            }
        });
    }

    public void loadGuideData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM guides";
            rs = stmt.executeQuery(sql);

            tableModel.setRowCount(0);
            while (rs.next()) {
                int id = rs.getInt("id_guide");
                String name = rs.getString("full_name");
                String phone = rs.getString("phone_number");
                String language = rs.getString("language");
                String status = rs.getString("status");

                tableModel.addRow(new Object[]{id, name, phone, language, status});
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

    private void deleteGuide(int guideId) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM guides WHERE id_guide = ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, guideId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Xoá hướng dẫn viên thành công!");
                loadGuideData();
            } else {
                JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi xoá hướng dẫn viên.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
