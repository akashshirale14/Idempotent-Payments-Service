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
        ResultSet resultSet=null;
        Statement statement = null;
        try {
            statement = dbConnection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM BalanceInfo");

            while(resultSet.next()) {
                String userid = resultSet.getString(1);
                int balance = resultSet.getInt(2);
                System.out.println("User: " + userid + " Balance: " + balance);
            }

        } catch (SQLException sqe) {
            System.out.println("Error while fetching data from DB: " + sqe);
        }finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException rr) {
                System.out.println("Error while closing");
            }
        }
     }

    public boolean checkIfSenderReceiverExists(String senderID, String receiverID,int amount) {
        try {
            String query = "SELECT * FROM BalanceInfo Where userid='" + senderID + "' OR userid='" + receiverID+"'";
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            //stmt.setString(1, "tanzo");
            //stmt.setString(2, "ashirale");

            ResultSet resultSet = stmt.executeQuery();
            System.out.println(query);
            int balance = 0;
            while(resultSet.next()) {
                String userid = resultSet.getString(1);
                System.out.println("Current check id:  " + userid);
                if (userid.equals(senderID)) {
                    balance = resultSet.getInt(2);
                    if (balance < amount) {
                        return false;
                    }
                }
            }
            return true;
        } catch (SQLException sqlException) {
            System.out.println("Error while validating sender/receiver : " + sqlException);
        }
        return false;
    }

    @Override
    public boolean doTransactionInDB(String transactionKey, String sender, String receiver, int amount, String opType) {
        try {
            dbConnection.setAutoCommit(false);

            PreparedStatement p1 = dbConnection.prepareStatement("UPDATE BalanceInfo SET balance=balance - ? WHERE userid=?");
            p1.setInt(1,amount);
            p1.setString(2,sender);
            int row1= p1.executeUpdate();

            PreparedStatement p2 = dbConnection.prepareStatement("UPDATE BalanceInfo SET balance=balance + ? WHERE userid=?");
            p2.setInt(1,amount);
            p2.setString(2,receiver);
            int row2 = p2.executeUpdate();

            if (row1==1 && row2==1) {
                dbConnection.commit();
                System.out.println("SQL Transaction completed");
            } else {
                dbConnection.rollback();
            }
            return true;
        } catch (SQLException sqlException) {
            System.out.println("SQL exception..Rollback transaction" + sqlException);
            try {
                dbConnection.rollback();
            } catch (SQLException ro) {
                System.out.println("Error while doing rollback: " + ro);
            }
            return false;
        } finally {
            try {
                if (dbConnection != null) {
                    dbConnection.setAutoCommit(true);
                }
            } catch (SQLException sqlex) {
                System.out.println("Error while setting autoCommit:true " + sqlex);
            }
        }
    }


}
