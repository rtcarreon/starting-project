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

package com.company.project.model;

import java.util.Date;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

/**
 *
 * @author Romer
 */
public class Token {
    private String username;
    private String series;
    private String tokenValue;
    private Date date;
 
    public Token() {
    }
 
    public Token(PersistentRememberMeToken persistentRememberMeToken) {
        this.username = persistentRememberMeToken.getUsername();
        this.series = persistentRememberMeToken.getSeries();
        this.date = persistentRememberMeToken.getDate();
        this.tokenValue = persistentRememberMeToken.getTokenValue();
    }
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getSeries() {
        return series;
    }
 
    public void setSeries(String series) {
        this.series = series;
    }
 
    public String getTokenValue() {
        return tokenValue;
    }
 
    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
 
    public Date getDate() {
        return date;
    }
 
    public void setDate(Date date) {
        this.date = date;
    }
}
