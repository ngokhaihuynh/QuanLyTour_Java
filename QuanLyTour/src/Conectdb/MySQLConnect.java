package Conectdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLConnect {
    private static String DB_URL = "jdbc:mysql://localhost:3306/tour_management";  // URL kết nối đến cơ sở dữ liệu
    private static String USER_NAME = "root";  // Username MySQL
    private static String PASSWORD = "";  // Mật khẩu MySQL (mặc định là rỗng trong XAMPP)

    // Phương thức kết nối tới database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
    }

    // Phương thức lấy danh sách người dùng từ bảng 'users'
    public static void fetchUsers() {
        Connection conn = null;
        PreparedStatement pr = null;
        ResultSet result = null;

        try {
            conn = getConnection();  // Lấy kết nối
            String sql = "SELECT * FROM users";  // Truy vấn lấy tất cả người dùng
            pr = conn.prepareStatement(sql);
            result = pr.executeQuery();

            while(result.next()) {
                // In ra các thông tin người dùng
                System.out.println("Username: " + result.getString("username"));
                System.out.println("Role: " + result.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();  // In ra lỗi nếu có
        } finally {
            try {
                // Đảm bảo đóng các tài nguyên sau khi sử dụng
                if (result != null) result.close();
                if (pr != null) pr.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        fetchUsers();  // Gọi phương thức để lấy danh sách người dùng
    }
}
