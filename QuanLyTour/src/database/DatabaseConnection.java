package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Thông tin kết nối cơ sở dữ liệu
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tour_management";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "";

    // Phương thức để lấy kết nối đến cơ sở dữ liệu
    public static Connection getConnection() throws SQLException {
        try {
            // Tải Driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Trả về kết nối
            return DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            // In lỗi nếu có vấn đề trong kết nối
            e.printStackTrace();
            throw new SQLException("Kết nối đến cơ sở dữ liệu thất bại!");
        }
    }
}
