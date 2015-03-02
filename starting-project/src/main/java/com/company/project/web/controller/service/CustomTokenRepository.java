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

package com.company.project.web.controller.service;

import com.company.project.service.dao.PersistentTokenDao;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Service;

/**
 *
 * @author Romer
 */
@Component
public class CustomTokenRepository implements PersistentTokenRepository {

    @Autowired
    private PersistentTokenDao persistentTokenDao;
    
    @Override
    public void createNewToken(PersistentRememberMeToken persistentRememberMeToken) {
        try{
            // username, series, token, date
            persistentTokenDao.insert(
                    persistentRememberMeToken.getUsername(),
                    persistentRememberMeToken.getSeries(),
                    persistentRememberMeToken.getTokenValue(),
                    persistentRememberMeToken.getDate()
            );
        } catch(Exception e){
            e.printStackTrace();
        } finally{

        }
    }
    
    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        try{
            // series, token, date
            persistentTokenDao.update(series, tokenValue, lastUsed);
        } catch(Exception e){
            e.printStackTrace();
        } finally{}
    }
 
    @Override
    public PersistentRememberMeToken getTokenForSeries(String series) {
        PersistentRememberMeToken persistentRememberMeToken = null;
        try{
            // series, token, date
            Map<String,Object> map = persistentTokenDao.get(series);
            if (map != null) {
                persistentRememberMeToken = new PersistentRememberMeToken(
                    "" + map.get("username"),
                    "" + map.get("series"),
                    "" + map.get("token"),
                    (Date) map.get("last_used")
                );
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally{}
        
        return persistentRememberMeToken;
    }
 
    @Override
    public void removeUserTokens(String username) {
        try{
            // series, token, date
            persistentTokenDao.delete(username);
        } catch(Exception e){
            e.printStackTrace();
        } finally{}
    }
}
