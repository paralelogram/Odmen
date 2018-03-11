package wrap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBController {
    public Statement statement;
    private static String userName;
    private static String password;
    private static String connectionUrl;
    public boolean isConnected;


    public DBController() {
    }

    public DBController(String userName, String password, String connectionUrl) {
        this.userName = userName;
        this.password = password;
        this.connectionUrl = connectionUrl;
    }

    public void connection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
        isConnected = true;
        statement = connection.createStatement();
    }

}
