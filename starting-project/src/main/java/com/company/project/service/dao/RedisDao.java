/*
 * Copyright 2014 Romer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.company.project.service.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Spring Redis Template implementation
 * 
 * redis data types ref: http://redis.io/topics/data-types
 * @author Romer
 */
@Repository
public class RedisDao {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void delete(String key) {
        redisTemplate.delete(key);
    }
    
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }
    
    public void set(String key, Object value) {
        ValueOperations opsForValue = redisTemplate.opsForValue();
        opsForValue.set(key, value);
    }
    
    public void setIfAbsent(String key, Object value) {
        ValueOperations opsForValue = redisTemplate.opsForValue();
        boolean isAbsent = opsForValue.setIfAbsent(key, value);
        if (isAbsent) {
            long sub = opsForValue.increment(key, 11);
        }
    }
    
    public Object get(String key) {
        ValueOperations opsForValue = redisTemplate.opsForValue();
        return opsForValue.get(key);
    }
    
    public void multiSet(Map<String, Object> keyValues) {
        ValueOperations opsForValue = redisTemplate.opsForValue();
        opsForValue.multiSet(keyValues);
    }
    
    public List multiGet(Collection<String> keys) {
        ValueOperations opsForValue = redisTemplate.opsForValue();
        return opsForValue.multiGet(keys);
    }
    
    /**
     * Add value to set
     * @param key
     * @param value
     * @return 
     */
    public long addUsingOpsForSet(String key, Object value) {
        SetOperations opsForSet = redisTemplate.opsForSet();
        return opsForSet.add(key, value);
    }
    
    /**
     * 4.9. Redis Transactions - http://docs.spring.io/spring-data/redis/docs/current/reference/html/
     * Add value to set. Utilize Session Callback
     * @param key
     * @param value
     * @return 
     */
    public long addUsingOpsForSetUsingSesssionCallback(final String key, final Object value) {
        //execute a transaction
        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForSet().add(key, value);

                // This will contain the results of all ops in the transaction
                return operations.exec();
            }
        });
        
        if (txResults != null && !txResults.isEmpty()) {
            System.out.println("Number of items added to set: " + txResults.get(0));
            try {
                int itemsAdded = Integer.parseInt("" + txResults.get(0));
                return itemsAdded;
            } catch(NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        
        return 0;
    }
    
    /**
     * 4.10. Pipelining - http://docs.spring.io/spring-data/redis/docs/current/reference/html/
     * @param key
     * @return 
     */
    public List<Object> pipelining(final String key) {
        //pop a specified number of items from a queue
        List<Object> results = redisTemplate.executePipelined(
            new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                    for (int i = 0; i < 10; i++) {
                        stringRedisConn.rPop(key);
                    }
                    return null;
                }
            });
        
        return results;
    }
    
    public Long databaseSize() {
        return (Long) redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.dbSize();
            }           
        });
    }
    
    /**
     * Get the value and remove from set
     * @param key
     * @return 
     */
    public Object popUsingOpsForSet(String key) {
        SetOperations opsForSet = redisTemplate.opsForSet();
        return opsForSet.pop(key);
    }
    
    /**
     * Combine element of sets from two keys
     * @param key1
     * @param key2
     * @return 
     */
    public Set unionUsingOpsForSet(String key1, String key2) {
        SetOperations opsForSet = redisTemplate.opsForSet();
        return opsForSet.union(key1, key2);
    }
    
    /**
     * Check if value is part of a set
     * @param key
     * @param value
     * @return 
     */
    public boolean isMemberUsingOpsForSet(String key, Object value) {
        SetOperations opsForSet = redisTemplate.opsForSet();
        return opsForSet.isMember(key, value);
    }
    
    /**
     * Remove value/s from set
     * @param key
     * @param values
     * @return 
     */
    public long removeUsingOpsForSet(String key, Object ... values) {
        SetOperations opsForSet = redisTemplate.opsForSet();
        return opsForSet.remove(key, values);
    }
    
    /**
     * get number of elements of a set
     * @param key
     * @return 
     */
    public long sizeUsingOpsForSet(String key) {
        SetOperations opsForSet = redisTemplate.opsForSet();
        return opsForSet.size(key);
    }
    
    /**
     * Transfer a value from one set to another
     * @param fromKey       source set
     * @param value         value to transfer
     * @param toKey         destination set
     * @return 
     */
    public boolean moveUsingOpsForSet(String fromKey, Object value, String toKey) {
        SetOperations opsForSet = redisTemplate.opsForSet();
        return opsForSet.move(fromKey, value, toKey);
    }
    
    /**
     * Get elements of set from given key
     * @param key
     * @return 
     */
    public Set membersUsingOpsForSet(String key) {
        SetOperations opsForSet = redisTemplate.opsForSet();
        return opsForSet.members(key);
    }
   
    /**
     * Push a value to the right of list
     * @param key
     * @param value 
     */
    public void rightPushToList(String key, String value) {
        ListOperations opsForList = redisTemplate.opsForList();
        opsForList.rightPush(key, value);
    }
   
    /**
     * Push a value to the left of list
     * @param key
     * @param value 
     */
    public void leftPushToList(String key, String value) {
        ListOperations opsForList = redisTemplate.opsForList();
        opsForList.leftPush(key, value);
    }

    /**
     * Pop a value from right of list
     * @param key
     * @return right most element of the list
     */
    public Object rightPopFromList(String key) {
        ListOperations opsForList = redisTemplate.opsForList();
        return opsForList.rightPop(key);
    }

     /**
     * Pop a value from left of list
     * @param key
     * @return left most element of the list
     */
    public Object leftPopFromList(String key) {
        ListOperations opsForList = redisTemplate.opsForList();
        return opsForList.leftPop(key);
    }

    /**
     * Remove an element from list
     * @param key
     * @param index
     * @param object 
     */
    public void removeFromList(String key, int index, Object object) {
        ListOperations opsForList = redisTemplate.opsForList();
        opsForList.remove(key, index, object);    //start and end of range. -1 means up to end of list
    }
    
     /**
     * Count elements of a list
     * @param       key
     * @return      number of element/s
     */
    public long countList(String key) {
        ListOperations opsForList = redisTemplate.opsForList();
        return opsForList.size(key);
    }
    
    /**
     * Get elements between start and end position of list
     * @param key
     * @param start
     * @param end       -1 means last element
     * @return 
     */
    public List multiGetList(String key, int start, int end) {
        ListOperations opsForList = redisTemplate.opsForList();
        return opsForList.range(key, start, end);    //start and end of range. -1 means up to end of list
    }
    
    /**
     * Remove elements from list 
     * @param key
     * @param start
     * @param end 
     */
    public void multiDeleteFromList(String key, int start, int end) {
        ListOperations opsForList = redisTemplate.opsForList();
        opsForList.trim(key, start, end);    //start and end of range. -1 means up to end of list
    }

    /**
     * Push a list to the left of list
     * - TODO unable to use "opsForList.trim(key, start, end)".
     * - use opsForList.leftPop(key) or opsForList.rightPop(key)
     * @param key
     * @param listValues 
     */
    public long leftPushAllList(String key, Object... listValues) {
        ListOperations opsForList = redisTemplate.opsForList();
        return opsForList.leftPushAll(key, listValues);
    }
    
    /**
     * Push a list to the right of list
     * - TODO unable to use "opsForList.trim(key, start, end)".
     * - use opsForList.leftPop(key) or opsForList.rightPop(key)
     * @param key
     * @param listValues 
     */
    public long rightPushAllList(String key, Object... listValues) {
        ListOperations opsForList = redisTemplate.opsForList();
        return opsForList.rightPushAll(key, listValues);
    }
    
    /**
     * 
     * @param objectKey ex. "USER", "GEM", "GROUP"
     * @param key       ex. "id of user", "id of gem", "id of group"
     * @param object 
     */
    public void setHash(String objectKey, String key, String object) {
        HashOperations opsForHash = redisTemplate.opsForHash();
        opsForHash.put(objectKey, key, object);
    }
    
    public Object getHash(String objectKey, String key) {
        HashOperations opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(objectKey, key);
    }
    
    public void deleteHash(Object objectKey, Object ... key) {
        HashOperations opsForHash = redisTemplate.opsForHash();
        opsForHash.delete(objectKey, key);
    }
    
    public void multiSetHash(String objectKey, Map map) {
        HashOperations opsForHash = redisTemplate.opsForHash();
        opsForHash.putAll(objectKey, map);
    }
    
    public List multiGetHash(String objectKey, Collection<String> keys) {
        HashOperations opsForHash = redisTemplate.opsForHash();
        return opsForHash.multiGet(objectKey, keys);
    }
    
    public boolean hasKeyHash(String objectKey, String key) {
        HashOperations opsForHash = redisTemplate.opsForHash();
        return opsForHash.hasKey(objectKey, key);
    }

    public Map entriesHash(String objectKey) {
        HashOperations opsForHash = redisTemplate.opsForHash();
        return opsForHash.entries(objectKey);
    }
    
    public Set keysHash(String objectKey) {
        HashOperations opsForHash = redisTemplate.opsForHash();
        return opsForHash.keys(objectKey);
    }
}
