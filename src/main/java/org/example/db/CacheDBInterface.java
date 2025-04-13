package org.example.db;

public interface CacheDBInterface {
    int transaction_ttl= 60;
    String transactionLog = "Transaction_Log";
    boolean validateIdempotencyKey(String idempotencyKey);
    boolean completeTransaction();
    boolean updateStateOfTransaction(String userid, String state);
    boolean saveIdempotencyKey(String key, String userid);
    boolean storeTransactionStatusInfo(String transType,String key,String senderID, String receiverID, String paymentAmt, String transStatus);
}
