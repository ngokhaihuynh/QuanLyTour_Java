package Home;

import User.UserManagement;
import Customer.CustomerManagement;
import Destinations.DestinationManagement;
import Tour.TourManagement;
import guides.GuideManagement;
import Booking.BookingManagement;
import Thongke.RevenuePanel;
import User.LoginForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainForm extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel contentPanel;
    private JButton[] buttons; // Thêm mảng các nút

    public MainForm() {
        setTitle("Hệ thống quản lý du lịch");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // JSplitPane: Chia cửa sổ thành hai phần
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(5);
        splitPane.setContinuousLayout(true);

        // Panel danh mục (bên trái)
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuPanel.setBackground(new Color(255, 253, 208)); // Màu nền kem

        // Các nút chức năng
        buttons = new JButton[] {
            new JButton("Quản lý người dùng"),
            new JButton("Quản lý khách hàng"),
            new JButton("Quản lý điểm đến"),
            new JButton("Quản lý hướng dẫn viên"),
            new JButton("Quản lý Tour"),
            new JButton("Quản lý Đặt Tour"),
            new JButton("Thống kê doanh thu")
        };

        // Lắng nghe sự kiện nút
        ActionListener[] listeners = {
            e -> showPanel(new UserManagement()),
            e -> showPanel(new CustomerManagement()),
            e -> showPanel(new DestinationManagement()),
            e -> showPanel(new GuideManagement()),
            e -> showPanel(new TourManagement()),
            e -> showPanel(new BookingManagement()),
            e -> showPanel(new RevenuePanel())
        };

        for (int i = 0; i < buttons.length; i++) {
            customizeButton(buttons[i]);
            buttons[i].addActionListener(listeners[i]);
            menuPanel.add(buttons[i]);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Panel hiển thị nội dung (bên phải)
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Thêm hình ảnh vào JLabel
        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/anhnha.jpg")); // Đảm bảo thư mục img nằm trong src
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(600, 450, Image.SCALE_SMOOTH); // Điều chỉnh kích thước hình ảnh lớn hơn
        lblLogo.setIcon(new ImageIcon(scaledImg));

        // Tạo một JPanel chứa nền màu kem cho chữ
        JPanel overlayPanel = new JPanel();
        overlayPanel.setLayout(new BorderLayout());
        overlayPanel.setBackground(new Color(255, 253, 208)); // Nền màu kem
        overlayPanel.setOpaque(true); // Đảm bảo panel có nền

        // Thêm chữ nổi bật lên ảnh
        JLabel titleLabel = new JLabel("Chào mừng bạn đến với hệ thống quản lý du lịch", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setForeground(Color.DARK_GRAY); // Chữ màu đen để dễ nhìn
        titleLabel.setOpaque(true);
        overlayPanel.add(titleLabel, BorderLayout.CENTER);

        // Tạo panel chứa ảnh và chữ
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        imagePanel.setBackground(new Color(255, 253, 208)); // Nền màu kem cho phần sau ảnh
        imagePanel.add(lblLogo, BorderLayout.CENTER); // Thêm hình ảnh vào giữa panel
        imagePanel.add(overlayPanel, BorderLayout.SOUTH); // Thêm phần chữ ở dưới ảnh

        // Thêm hình ảnh và chữ vào contentPanel
        contentPanel.add(imagePanel, BorderLayout.CENTER);

        // Gán menuPanel và contentPanel vào splitPane
        splitPane.setLeftComponent(menuPanel);
        splitPane.setRightComponent(contentPanel);

        // Thêm splitPane vào JFrame
        getContentPane().add(splitPane);
    }

    // Phương thức tùy chỉnh các nút
    private void customizeButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Tahoma", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBackground(new Color(34, 139, 34)); // Màu xanh lá cho nút
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(200, 40));
        button.setBorder(BorderFactory.createLineBorder(new Color(42, 122, 110), 1));
    }

    // Phương thức thay đổi màu sắc nút khi nhấn
    private void setActiveButton(JButton activeButton) {
        for (JButton button : buttons) {
            button.setBackground(new Color(34, 139, 34)); // Đặt lại màu nền xanh lá
        }
        activeButton.setBackground(new Color(255, 140, 0)); // Màu khi nút được nhấn
    }

    // Phương thức hiển thị giao diện theo từng chức năng
    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Kiểm tra trạng thái đăng nhập
    private boolean isLoggedIn() {
        return false; // Bạn có thể sửa lại logic kiểm tra đăng nhập của bạn ở đây
    }

    // Phương thức chính
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                if (new MainForm().isLoggedIn()) {
                    MainForm frame = new MainForm();
                    frame.setVisible(true);
                } else {
                    LoginForm loginForm = new LoginForm();
                    loginForm.setVisible(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
