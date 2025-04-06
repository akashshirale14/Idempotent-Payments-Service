package org.example.db;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

/*
This class contains all operations done on the cache DB - Redis here
 */
public class RedisCacheDBLayer implements CacheDBInterface {

    private Jedis db;
    private static String cacheTransaction = "Transactions";
    public RedisCacheDBLayer() {
        db = new Jedis("localhost", 6379);
    }
    @Override
    public boolean validateIdempotencyKey(String idempotencyKey) {
        if (db.exists(idempotencyKey)) {
            return false;
        } else {
            return true;
        }
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
            db.setex(key, CacheDBInterface.transaction_ttl, userid);
        } catch (JedisException je) {
            System.out.println("Error while saveing idempotency key.." + je);
            return false;
        }
        return true;
    }
}
