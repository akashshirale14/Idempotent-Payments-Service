package org.example.db;

public interface DBStoreInterface {
    void showData();
    boolean checkIfSenderReceiverExists(String senderID, String receiverID,int amount);
    boolean doTransactionInDB(String transactionKey, String sender, String receiver, int amount, String opType);
}
