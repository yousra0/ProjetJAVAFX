package Outil;

import java.sql.*;

public class DataBase {
    private Connection conn;
    final String url = "jdbc:mysql://localhost:3306/gami";
    final String user = "root";
    final String pwd = "";
    static DataBase instance;

    private DataBase() {
        try {
            conn = DriverManager.getConnection(url, user, pwd);
            System.out.println("Connected");
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }

    public static DataBase getInstance() {
        if (instance == null) {
            return instance = new DataBase();
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
}
