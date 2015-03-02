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

package com.company.project.service;

import com.company.project.persistence.UserMapper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * MyBatis API implementation
 * @author Romer
 */
@Service("userMapperImpl")
//@Service
@Transactional
public class UserMapperImpl implements UserMapper {
    
    //public UserMapperImpl() {}
    
// SqlSessionFactory would normally be set by SqlSessionDaoSupport
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

//    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
//        this.sqlSessionFactory = sqlSessionFactory;
//    }
    
    @Autowired
    @Qualifier("txManager")
    private PlatformTransactionManager txManager;

    @Override
    public Map get(String username) {
        // note standard MyBatis API usage - opening and closing the session manually
        SqlSession session = sqlSessionFactory.openSession();
        try {
            return session.selectOne("com.company.project.persistence.UserMapper.get", username);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return null;
    }
    
    @Override
    public List getAll() {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            return session.selectList("com.company.project.persistence.UserMapper.getAll");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return null;
    }
    
    @Override
    @Transactional("transactionManager")
    public int insert(Map user) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            return session.insert("com.company.project.persistence.UserMapper.insert", user);
        } catch(Exception e) {
            e.printStackTrace();
            session.rollback();
        } finally {
            session.close();
        }
        
        return 0;
    }
    
    @Transactional("txManager")
    public int insertx(Map user) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = txManager.getTransaction(def);
        
        SqlSession session = sqlSessionFactory.openSession();
        int result = 0;
        try {
            result = session.insert("com.company.project.persistence.UserMapper.insert", user);
            txManager.commit(status);                   // will not insert if this line is removed
            //txManager.rollback(status);
        } catch(Exception e) {
            e.printStackTrace();
            //session.rollback();
            txManager.rollback(status);
        } finally {
            session.close();
        }
        
        return result;
    }

    public int update(Map user) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            return session.update("com.company.project.persistence.UserMapper.update", user);
        } catch(Exception e) {
            e.printStackTrace();
            session.rollback();
        } finally {
            session.close();
        }
        
        return 0;
    }
    
    public int delete(String username) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            return session.delete("com.company.project.persistence.UserMapper.delete", username);
        } catch(Exception e) {
            e.printStackTrace();
            session.rollback();
        } finally {
            session.close();
        }
        
        return 0;
    }
}
