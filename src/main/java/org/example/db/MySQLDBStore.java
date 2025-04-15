package org.example.db;

import java.sql.*;

public class MySQLDBStore implements DBStoreInterface {

    private String url;
    private String username;
    private String password;
    private Connection dbConnection;
    public MySQLDBStore() {
        url="jdbc:mysql://localhost:3306/PaymentSystem";
        username = "root";
        password = "akash";
        try {
            dbConnection = DriverManager.getConnection(url, username, password);
        } catch (SQLException sqle) {
            System.out.println("Error while connecting to DB: " + sqle);
        }
        showData();
    }
    public void showData() {
        try {
            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM BalanceInfo");

            while(resultSet.next()) {
                String userid = resultSet.getString(1);
                int balance = resultSet.getInt(2);
                System.out.println("User: " + userid + " Balance: " + balance);
            }

        } catch (SQLException sqe) {
            System.out.println("Error while fetching data from DB: " + sqe);
        }
     }








}
