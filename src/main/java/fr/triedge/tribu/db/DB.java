package fr.triedge.tribu.db;

import com.idorsia.research.sbilib.utils.SPassword;
import fr.triedge.tribu.model.Conversation;
import fr.triedge.tribu.model.Message;
import fr.triedge.tribu.model.User;
import fr.triedge.tribu.utils.Utils;

import java.sql.*;
import java.util.ArrayList;

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

    public ArrayList<Conversation> getCurrentConversations(User user) throws SQLException {
        if (user == null)
            return null;
        ArrayList<Conversation> convs = new ArrayList<>();
        String sql = "select * from tconvers where convers_sender=? or convers_receiver=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, user.getId());
        stmt.setInt(2, user.getId());
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            int convId = res.getInt("convers_id");
            int senderId = res.getInt("convers_sender");
            int receiverId = res.getInt("convers_receiver");
            Conversation c = new Conversation();
            c.setId(convId);
            c.setSender(getUser(senderId));
            c.setReceiver(getUser(receiverId));
            c.setMessages(getMessagesForConversation(convId));
        }
        res.close();
        stmt.close();
        return convs;
    }

    private User getUser(int id) throws SQLException {
        User user = null;
        String sql = "select * from tuser where user_id=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet res = stmt.executeQuery();
        user = createUser(res);
        res.close();
        stmt.close();
        return user;
    }

    private User createUser(ResultSet res) throws SQLException {
        User user = null;
        if (res.next()){
            user = new User();
            user.setId(res.getInt("user_id"));
            user.setName(res.getString("user_name"));
            user.setEmail(res.getString("user_email"));
            user.setImage(res.getString("user_img"));
        }
        return user;
    }

    public User loadUser(String username) throws SQLException {
        User user = null;
        String sql = "select * from tuser where user_name=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet res = stmt.executeQuery();
        user = createUser(res);
        res.close();
        stmt.close();
        return user;
    }

    public User getUser(String email, String password) throws SQLException {
        User user = null;
        String sql = "select * from tuser where user_email=? and user_password=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setString(1, email);
        stmt.setString(2, new SPassword(password).getEncrypted());
        ResultSet res = stmt.executeQuery();
        user = createUser(res);
        res.close();
        stmt.close();
        return user;
    }

    private ArrayList<Message> getMessagesForConversation(int convId) throws SQLException {
        ArrayList<Message> mess = new ArrayList<>();
        String sql = "select * from tmessage where message_convers=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1,convId);
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            Message mes = new Message();
            mes.setId(res.getInt("message_id"));
            mes.setContent(res.getString("message_content"));
            Timestamp timestamp = res.getTimestamp("message_date");
            if (timestamp != null){
                mes.setDate(Utils.milliToLocalDateTime(timestamp.getTime()));

            }
            mess.add(mes);
        }
        res.close();
        stmt.close();
        return mess;
    }
}
