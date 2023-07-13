package fr.triedge.tribu.db;

import com.idorsia.research.sbilib.utils.SPassword;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static DB instance;
    private Connection connection;

    private DB(){}

    public static DB getInstance(){
        if (instance == null){
            instance = new DB();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()){
            resetConnection();
        }
        return connection;
    }

    public void resetConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        SPassword password = new SPassword("c2JpLSRiaXVzZXJTIzg4");
        String pwd = password.getDecrypted();
        String host = "localhost";
        if (System.getProperty("host") != null){
            host = System.getProperty("host");
        }
        connection = DriverManager.getConnection("jdbc:mysql://"+host+"/tribu","tribu",pwd);
    }
}
