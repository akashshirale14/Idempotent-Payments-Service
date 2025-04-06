package org.example;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.db.CacheDBInterface;
import org.example.db.RedisCacheDBLayer;

public class PaymentsService {

    private CacheDBInterface cacheDBInstance;
    public PaymentsService() {
        cacheDBInstance =  new RedisCacheDBLayer();
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

}
