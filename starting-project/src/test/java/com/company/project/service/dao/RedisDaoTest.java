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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author Romer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class RedisDaoTest {
    
    @Configuration
    @ComponentScan(basePackages = "com.company.project")
    public static class Config {}
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Autowired
    private RedisDao redisDao;

    /**
     * Test of get method, of class RedisDao.
     */
    @Test
    public void testSetGet() {
        System.out.println("get");
        String key = "thekey";
        String value = "theValue";
        redisDao.set(key, value);
        
        Object object = redisDao.get(key);
        assertEquals("" + object, value);
        
        System.out.println("Object:" + object);
        
        redisDao.delete(key);
        
        object = redisDao.get(key);
        assertNull(object);
    }
    
    @Test
    public void testSetGetMulti() {
        System.out.println("get");
        Map<String, Object> map = new HashMap<>();
        map.put("thekey1", "theValue1");
        map.put("thekey2", "theValue2");
        map.put("thekey3", "theValue3");
        
        redisDao.multiSet(map);

        List list = redisDao.multiGet(map.keySet());
        assertEquals(map.keySet().size(), list.size());
        for (Object string : list) {
            assertTrue(map.containsValue("" + string));
        }
        System.out.println("List:" + list);
        
        redisDao.delete(map.keySet());
        
        list = redisDao.multiGet(map.keySet());
        for (Object string : list) {
            assertNull(string);
        }
        System.out.println("List after delete:" + list);
    }
    
    @Test
    public void testPushPopToList() {
        System.out.println("testPushPopFromListOne");
        String key = "thekeyList";
        String value = "theValueList1";
        
        // ensure list is empty
        redisDao.multiDeleteFromList(key, 0, -1);
        
        long count = redisDao.countList(key);
        assertEquals(0, count);
        
        redisDao.rightPushToList(key, value);
        count = redisDao.countList(key);
        assertEquals(1, count);
        
        value = "theValueList2";
        redisDao.rightPushToList(key, value);
        count = redisDao.countList(key);
        assertEquals(2, count);
        
        value = "theValueList3";
        redisDao.rightPushToList(key, value);
        count = redisDao.countList(key);
        assertEquals(3, count);
        
        // (key, 0, 0)will return the first (left) item, (key, -1, -1) will return the last (right) 
        List list = redisDao.multiGetList(key, 0, 0);     
        assertNotNull(list);
        assertEquals(1, list.size());
        assertTrue(list.contains("theValueList1"));
        
        list = redisDao.multiGetList(key, 0, 1);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.contains("theValueList1"));
        assertTrue(list.contains("theValueList2"));
        
        list = redisDao.multiGetList(key, 0, 2);      // -1 mean up to last element
        assertNotNull(list);
        assertEquals(3, list.size());
        assertTrue(list.contains("theValueList1"));
        assertTrue(list.contains("theValueList2"));
        assertTrue(list.contains("theValueList3"));
        
        list = redisDao.multiGetList(key, 0, -1);      // -1 mean up to last element
        assertNotNull(list);
        assertEquals(3, list.size());
        assertTrue(list.contains("theValueList1"));
        assertTrue(list.contains("theValueList2"));
        assertTrue(list.contains("theValueList3"));

        // get and remove element
        Object object = redisDao.rightPopFromList(key);
        assertEquals("theValueList3", "" + object);
        
         // get and remove element
        object = redisDao.rightPopFromList(key);
        assertEquals("theValueList2", "" + object);
        
        // remove a value from list
        redisDao.removeFromList(key, 0, "theValueList1");
        
        //object = redisDao.rightPopList(key);
        //assertEquals("theValueList1", "" + object);
        
        object = redisDao.rightPopFromList(key);
        assertNull(object);
        
        object = redisDao.rightPopFromList(key);
        assertNull(object);
        
        object = redisDao.rightPopFromList(key);
        assertNull(object);
        
        object = redisDao.rightPopFromList(key);
        assertNull(object);
        
        // delete all elements
        redisDao.multiDeleteFromList(key, 0, -1);
    }

    @Test
    public void testPushPopListToList() {
        System.out.println("get");
        String key = "thekeyList2";
        
        // ensure list is empty
        redisDao.multiDeleteFromList(key, 0, -1);
        
        System.out.println("Initial Count:" + redisDao.countList(key));
        
        //redisDao.delete(key);
        List list = new ArrayList();
        list.add("theValueList1");
        list.add("theValueList2");
        list.add("theValueList3");
        
        redisDao.leftPushAllList(key, list);
        
        long count = redisDao.countList(key);
        assertEquals(1, count);

        list = new ArrayList();
        list.add("theValueList4");
        list.add("theValueList5");
        list.add("theValueList6");
        
        redisDao.rightPushAllList(key, list);
        
        count = redisDao.countList(key);
        assertEquals(2, count);
        System.out.println("Count list:" + redisDao.countList(key));
        
        List listResult = redisDao.multiGetList(key, 0, -1);
        assertNotNull(listResult);
        assertEquals(2, listResult.size());
        
        assertTrue(((List)listResult.get(0)).contains("theValueList1"));
        assertTrue(((List)listResult.get(0)).contains("theValueList2"));
        assertTrue(((List)listResult.get(0)).contains("theValueList3"));
        
        assertTrue(((List)listResult.get(1)).contains("theValueList4"));
        assertTrue(((List)listResult.get(1)).contains("theValueList5"));
        assertTrue(((List)listResult.get(1)).contains("theValueList6"));
        
        Object object = redisDao.rightPopFromList(key);
        System.out.println("Object:" + object);
        assertTrue(((List)object).contains("theValueList4"));
        assertTrue(((List)object).contains("theValueList5"));
        assertTrue(((List)object).contains("theValueList6"));
        
        object = redisDao.rightPopFromList(key);
        assertTrue(((List)object).contains("theValueList1"));
        assertTrue(((List)object).contains("theValueList2"));
        assertTrue(((List)object).contains("theValueList3"));
        
        redisDao.multiDeleteFromList(key, 0, -1);// this doesn't clear the data. TODO cannot remove all
    }
    
    @Test
    public void testSetGetDeleteHash() {
        System.out.println("get");
        String objectKey = "objectKeyHash";
        String key = "keyHash";
        String value = "valueHash";

        redisDao.setHash(objectKey, key, value);
        
        Object object = redisDao.getHash(objectKey, key);
        assertEquals(value, "" + object);
        
        value = "valueHashx";
        redisDao.setHash(objectKey, key, value);
        object = redisDao.getHash(objectKey, key);
        assertEquals(value, "" + object);

        redisDao.deleteHash(objectKey, key);
        
        object = redisDao.getHash(objectKey, key);
        assertNull(object);
    }
    
    @Test
    public void testMultiSetGetDeleteHash() {
        System.out.println("get");
        String objectKey = "objectKeyHash";
        Map map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.put("key4", "value4");

        redisDao.multiSetHash(objectKey, map);
        
        List values = redisDao.multiGetHash(objectKey, map.keySet());
        assertEquals(map.values().size(), values.size());
        for (Object object : values) {
            assertTrue(map.containsValue(object));
        }
        
        boolean hasKey = redisDao.hasKeyHash(objectKey, "key1");
        assertTrue(hasKey);
        hasKey = redisDao.hasKeyHash(objectKey, "key2");
        assertTrue(hasKey);
        hasKey = redisDao.hasKeyHash(objectKey, "key3");
        assertTrue(hasKey);
        hasKey = redisDao.hasKeyHash(objectKey, "key4");
        assertTrue(hasKey);
        
        Map entries = redisDao.entriesHash(objectKey);
        assertNotNull(entries);
        for (Object object : entries.keySet()) {
             assertTrue(map.containsKey(object));
        }
        for (Object object : entries.values()) {
             assertTrue(map.containsValue(object));
        }
        
        Set entriesKey = redisDao.keysHash(objectKey);
        assertNotNull(entriesKey);
        assertEquals(4, entriesKey.size());
        assertTrue(entriesKey.contains("key1"));
        assertTrue(entriesKey.contains("key2"));
        assertTrue(entriesKey.contains("key3"));
        assertTrue(entriesKey.contains("key4"));
        
        
        Object object = redisDao.getHash(objectKey, "key1");
        assertNotNull(object);
        assertEquals("value1", "" + object);
        
        object = redisDao.getHash(objectKey, "key2");
        assertNotNull(object);
        assertEquals("value2", "" + object);
        
        object = redisDao.getHash(objectKey, "key3");
        assertNotNull(object);
        assertEquals("value3", "" + object);
        
        object = redisDao.getHash(objectKey, "key4");
        assertNotNull(object);
        assertEquals("value4", "" + object);

        redisDao.deleteHash(objectKey, "key1", "key2", "key3", "key4");
        
        object = redisDao.getHash(objectKey, "key1");
        assertNull(object);
        
        object = redisDao.getHash(objectKey, "key2");
        assertNull(object);
        
        object = redisDao.getHash(objectKey, "key3");
        assertNull(object);
        
        object = redisDao.getHash(objectKey, "key4");
        assertNull(object);
    }
    
    @Test
    public void testAddPopFromSet() {
        System.out.println("testAddPopFromSet");
        String k1 = "key1";
        String k1v1 = "key1 value1";
        String k1v2 = "key1 value2";
        String k1v3 = "key1 value3";
        String k2 = "key2";
        String k2v1 = "key2 value1";
        String k2v2 = "key2 value2";
        String k2v3 = "key2 value3";
        String k3 = "key3";
        String k3v1 = "key3 value1";
        String k3v2 = "key3 value2";
        String k3v3 = "key3 value3";
        
        /* Add values to key 1 */
        long result = redisDao.addUsingOpsForSet(k1, k1v1);
        assertEquals(1, result);
        long count = redisDao.sizeUsingOpsForSet(k1);
        assertEquals(1, count);
        boolean isMember = redisDao.isMemberUsingOpsForSet(k1, k1v1);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k1, k1v2);
        assertFalse(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k1, k1v3);
        assertFalse(isMember);
        Set members = redisDao.membersUsingOpsForSet(k1);
        assertEquals(1, members.size());
        assertTrue(members.contains(k1v1));
        assertFalse(members.contains(k1v2));
        assertFalse(members.contains(k1v3));
         
        result = redisDao.addUsingOpsForSet(k1, k1v2);
        assertEquals(1, result);
        count = redisDao.sizeUsingOpsForSet(k1);
        assertEquals(2, count);
        isMember = redisDao.isMemberUsingOpsForSet(k1, k1v1);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k1, k1v2);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k1, k1v3);
        assertFalse(isMember);
        members = redisDao.membersUsingOpsForSet(k1);
        assertEquals(2, members.size());
        assertTrue(members.contains(k1v1));
        assertTrue(members.contains(k1v2));
        assertFalse(members.contains(k1v3));
         
        result = redisDao.addUsingOpsForSet(k1, k1v3);
        assertEquals(1, result);
        count = redisDao.sizeUsingOpsForSet(k1);
        assertEquals(3, count);
        isMember = redisDao.isMemberUsingOpsForSet(k1, k1v1);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k1, k1v2);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k1, k1v3);
        assertTrue(isMember);
        members = redisDao.membersUsingOpsForSet(k1);
        assertEquals(3, members.size());
        assertTrue(members.contains(k1v1));
        assertTrue(members.contains(k1v2));
        assertTrue(members.contains(k1v3));
        
        // test add existing
        result = redisDao.addUsingOpsForSet(k1, k1v3);
        assertEquals(0, result);
        count = redisDao.sizeUsingOpsForSet(k1);
        assertEquals(3, count);
        isMember = redisDao.isMemberUsingOpsForSet(k1, k1v1);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k1, k1v2);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k1, k1v3);
        assertTrue(isMember);
        members = redisDao.membersUsingOpsForSet(k1);
        assertEquals(3, members.size());
        assertTrue(members.contains(k1v1));
        assertTrue(members.contains(k1v2));
        assertTrue(members.contains(k1v3));
         
        /* Add values to key 2 */
        result = redisDao.addUsingOpsForSet(k2, k2v1);
        assertEquals(1, result);
        count = redisDao.sizeUsingOpsForSet(k2);
        assertEquals(1, count);
        isMember = redisDao.isMemberUsingOpsForSet(k2, k2v1);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k2, k2v2);
        assertFalse(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k2, k2v3);
        assertFalse(isMember);
        members = redisDao.membersUsingOpsForSet(k2);
        assertEquals(1, members.size());
        assertTrue(members.contains(k2v1));
        assertFalse(members.contains(k2v2));
        assertFalse(members.contains(k2v3));
         
        result = redisDao.addUsingOpsForSet(k2, k2v2);
        assertEquals(1, result);
        count = redisDao.sizeUsingOpsForSet(k2);
        assertEquals(2, count);
        isMember = redisDao.isMemberUsingOpsForSet(k2, k2v1);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k2, k2v2);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k2, k2v3);
        assertFalse(isMember);
        members = redisDao.membersUsingOpsForSet(k2);
        assertEquals(2, members.size());
        assertTrue(members.contains(k2v1));
        assertTrue(members.contains(k2v2));
        assertFalse(members.contains(k2v3));
         
        result = redisDao.addUsingOpsForSet(k2, k2v3);
        assertEquals(1, result);
        count = redisDao.sizeUsingOpsForSet(k2);
        assertEquals(3, count);
        isMember = redisDao.isMemberUsingOpsForSet(k2, k2v1);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k2, k2v2);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k2, k2v3);
        assertTrue(isMember);
        members = redisDao.membersUsingOpsForSet(k2);
        assertEquals(3, members.size());
        assertTrue(members.contains(k2v1));
        assertTrue(members.contains(k2v2));
        assertTrue(members.contains(k2v3));
        
        // test add existing
        result = redisDao.addUsingOpsForSet(k2, k2v3);
        assertEquals(0, result);
        count = redisDao.sizeUsingOpsForSet(k2);
        assertEquals(3, count);
        isMember = redisDao.isMemberUsingOpsForSet(k2, k2v1);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k2, k2v2);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k2, k2v3);
        assertTrue(isMember);
        members = redisDao.membersUsingOpsForSet(k2);
        assertEquals(3, members.size());
        assertTrue(members.contains(k2v1));
        assertTrue(members.contains(k2v2));
        assertTrue(members.contains(k2v3));
        
        /* Add values to key 3 */
        result = redisDao.addUsingOpsForSet(k3, k3v1);
        assertEquals(1, result);
        count = redisDao.sizeUsingOpsForSet(k3);
        assertEquals(1, count);
        isMember = redisDao.isMemberUsingOpsForSet(k3, k3v1);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k3, k3v2);
        assertFalse(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k3, k3v3);
        assertFalse(isMember);
        members = redisDao.membersUsingOpsForSet(k3);
        assertEquals(1, members.size());
        assertTrue(members.contains(k3v1));
        assertFalse(members.contains(k3v2));
        assertFalse(members.contains(k3v3));
         
        result = redisDao.addUsingOpsForSet(k3, k3v2);
        assertEquals(1, result);
        count = redisDao.sizeUsingOpsForSet(k3);
        assertEquals(2, count);
        isMember = redisDao.isMemberUsingOpsForSet(k3, k3v1);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k3, k3v2);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k3, k3v3);
        assertFalse(isMember);
        members = redisDao.membersUsingOpsForSet(k3);
        assertEquals(2, members.size());
        assertTrue(members.contains(k3v1));
        assertTrue(members.contains(k3v2));
        assertFalse(members.contains(k3v3));
         
        result = redisDao.addUsingOpsForSet(k3, k3v3);
        assertEquals(1, result);
        count = redisDao.sizeUsingOpsForSet(k3);
        assertEquals(3, count);
        isMember = redisDao.isMemberUsingOpsForSet(k3, k3v1);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k3, k3v2);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k3, k3v3);
        assertTrue(isMember);
        members = redisDao.membersUsingOpsForSet(k3);
        assertEquals(3, members.size());
        assertTrue(members.contains(k3v1));
        assertTrue(members.contains(k3v2));
        assertTrue(members.contains(k3v3));
        
        // test add existing
        result = redisDao.addUsingOpsForSet(k3, k3v3);
        assertEquals(0, result);
        count = redisDao.sizeUsingOpsForSet(k3);
        assertEquals(3, count);
        isMember = redisDao.isMemberUsingOpsForSet(k3, k3v1);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k3, k3v2);
        assertTrue(isMember);
        isMember = redisDao.isMemberUsingOpsForSet(k3, k3v3);
        assertTrue(isMember);
        members = redisDao.membersUsingOpsForSet(k3);
        assertEquals(3, members.size());
        assertTrue(members.contains(k3v1));
        assertTrue(members.contains(k3v2));
        assertTrue(members.contains(k3v3));
        
        /* Combine 2 sets */
        Set combinedSet = redisDao.unionUsingOpsForSet(k1, k2);
        assertEquals(6, combinedSet.size());
        assertTrue(combinedSet.contains(k1v1));
        assertTrue(combinedSet.contains(k1v2));
        assertTrue(combinedSet.contains(k1v3));
        assertTrue(combinedSet.contains(k2v1));
        assertTrue(combinedSet.contains(k2v2));
        assertTrue(combinedSet.contains(k2v3));
        
        /* verify members of key 1 and key 2*/
        members = redisDao.membersUsingOpsForSet(k1);
        assertEquals(3, members.size());
        assertTrue(members.contains(k1v1));
        assertTrue(members.contains(k1v2));
        assertTrue(members.contains(k1v3));
        members = redisDao.membersUsingOpsForSet(k2);
        assertEquals(3, members.size());
        assertTrue(members.contains(k2v1));
        assertTrue(members.contains(k2v2));
        assertTrue(members.contains(k2v3));
        
        /* get and remove a value from key 1*/
        Object valueK1 = redisDao.popUsingOpsForSet(k1);
        assertNotNull(valueK1);
        assertTrue(valueK1.equals(k1v1) || valueK1.equals(k1v2) || valueK1.equals(k1v3));
        
        /* verify members of key 1 after pop */
        members = redisDao.membersUsingOpsForSet(k1);
        assertEquals(2, members.size());
        assertFalse(members.contains(valueK1));

        /* remove element from set */
        long removed = redisDao.removeUsingOpsForSet(k1, members.toArray()[0]);
        assertEquals(1, removed);
        
        /* verify members of key 1 after remove */
        Object lastMember = members.toArray()[1];
        members = redisDao.membersUsingOpsForSet(k1);
        assertEquals(1, members.size());
        assertTrue(members.contains(lastMember));
        
        /* move last member of key 1 to key 2 */
        redisDao.moveUsingOpsForSet(k1, lastMember, k2);
        
        /* verify members of key 1 after move */
        members = redisDao.membersUsingOpsForSet(k1);
        assertTrue(members.isEmpty());

        /* verify members of key 2 after move */
        members = redisDao.membersUsingOpsForSet(k2);
        assertFalse(members.isEmpty());
        assertEquals(4, members.size());
        assertTrue(members.contains(lastMember));
        assertTrue(members.contains(k2v1));
        assertTrue(members.contains(k2v2));
        assertTrue(members.contains(k2v3));
        
        removed = redisDao.removeUsingOpsForSet(k2, lastMember, k2v1, k2v2, k2v3);
        assertEquals(4, removed);
        
        removed = redisDao.removeUsingOpsForSet(k3, k3v1, k3v2, k3v3);
        assertEquals(3, removed);
     }
}
