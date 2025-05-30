package org.example;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.db.CacheDBInterface;
import org.example.db.DBStoreInterface;
import org.example.db.MySQLDBStore;
import org.example.db.RedisCacheDBLayer;

enum TransactionStatus {
    STARTED("Started"),
    BEFORE_DB("Before_DB_Op"),
    AFTER_DB("After_DB_Op"),
    AFTER_DBLOG("After_DB_Log"),
    DONE("Done");

    TransactionStatus(String state) {
    }
}

enum TransactionType {
    Payment("Payment"),
    MONEY_ORDER("Money_Order");
    TransactionType(String name){
    }
}

class Transaction {}

public class PaymentsService {

    private CacheDBInterface cacheDBInstance;
    private DBStoreInterface dbStoreInstance;
    public PaymentsService() {
        cacheDBInstance =  new RedisCacheDBLayer();
        dbStoreInstance = new MySQLDBStore();
    }

    public String generateIdempotencyKey(String userid) {
        String key = userid + System.currentTimeMillis();
        String hashKey = DigestUtils.md5Hex(key);
        System.out.println("Hash: " + hashKey);
        boolean status = cacheDBInstance.saveIdempotencyKey(hashKey, userid);
        if (!status) {
            return null;
        }
        return hashKey;
    }

    public boolean validateTransactionStatus(String transactionKey) {
        //validate idempotency-key
        boolean validationStatus = cacheDBInstance.validateIdempotencyKey(transactionKey);
        if (!validationStatus) {
            return false;
        }
        return validationStatus;
    }

    public String storeTransactionStatusInfo(String transType,String key, String senderID, String receiverID,String amount) {
        //Validate amount
        try {
            Double.parseDouble(amount);
        } catch (NumberFormatException nfe) {
            return "Invalid Amount Value";
        }
        //Validate Receiver from SQL : Store in Redis for quick lookup


        //Validate TransactionType
        System.out.println("transType: " + transType + " compare: " + TransactionType.Payment.name());
        if(!transType.equals(TransactionType.Payment.name())) {
            return "Invalid Operation";
        }

        //Store transaction Info in Redis
        boolean status = cacheDBInstance.storeTransactionStatusInfo(transType,key,senderID,receiverID,amount,TransactionStatus.STARTED.toString());
        if(!status) {
            return "Error storing Info. Try again later.";
        }

        return "Success";
    }

    public String doTransactionInDB(String transactionKey, String senderID,String receiverID,int amount,String opType){
        boolean checkValid = dbStoreInstance.checkIfSenderReceiverExists(senderID,receiverID, amount);
        System.out.println("CheckValid: " + checkValid);
        if (!checkValid) {
            return "Invalid Transaction: Check Sender & Receiver Is Valid or Not";
        }
        //Write to BalanceInfo & TransactionLog table

        boolean status = dbStoreInstance.doTransactionInDB(transactionKey,senderID,receiverID,amount,opType);
        if (status) {
            return "Success: Money transfer was successful";
        }
        return "Failure";
    }



}
