package org.example.db;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

/*
This class contains all operations done on the cache DB - Redis here
 */


public class RedisCacheDBLayer implements CacheDBInterface {

    private Jedis db;
    private static String cacheTransaction = "Transactions";
    private static String transactionKey= "TK_";
    private static String statusKey="SK_";
    public RedisCacheDBLayer() {
        db = new Jedis("localhost", 6379);
    }

    @Override
    public boolean completeTransaction() {
        return false;
    }

    @Override
    public boolean updateStateOfTransaction(String userid, String state) {
        return false;
    }

    @Override
    public boolean saveIdempotencyKey(String key, String userid) {
        try {
            db.setex(transactionKey + key, CacheDBInterface.transaction_ttl, userid);
        } catch (JedisException je) {
            System.out.println("Error while saveing idempotency key.." + je);
            return false;
        }
        return true;
    }

    public boolean validateIdempotencyKey(String key) {
        try {
            boolean keyCheck = db.exists(transactionKey + key);
            return keyCheck;
        } catch (JedisException je) {
            System.out.println("Error while validating transaction key: " + je);
        }
        return false;
    }

    public boolean storeTransactionStatusInfo(String transType,String key,String senderID, String receiverID, String paymentAmt, String transStatus) {
        try {
            db.set(statusKey + key, transType+"-"+key+"-"+senderID+"-"+receiverID +"-"+ paymentAmt +":"+ transStatus);
        } catch (JedisException je) {
            System.out.println("Error while storing Transaction status: " + je);
            return false;
        }
        return true;
    }
}
