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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Romer
 */
@Service("customAuthenticationProvider")
public class CustomAuthenticationProvider implements AuthenticationProvider {
    
    @Autowired
    //@Resource(name="customUserDetailsService")
    @Qualifier("customUserDetailsService")
    private CustomUserDetailsService customUserDetailsService;
 
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        // CustomUserDetailsService will take care of password comparison
        // return null if username is not existing or password comparison fails
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(name);
        
        if (userDetails == null) {
            throw new BadCredentialsException("Username not found or password incorrect.");
        }

        if (userDetails !=  null) {

            // 3. Preferably clear the password in the user object before storing in authentication object           
            //return new UsernamePasswordAuthenticationToken(name, null, userDetails.getAuthorities());
            // OR
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            
            // use authentication.getPrincipal() to get the "userDetails" object
        } 
        return null;
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

//    /**
//     * @param customUserDetailsService the customUserDetailsService to set
//     */
//    public void setCustomUserDetailsService(CustomUserDetailsService customUserDetailsService) {
//        this.customUserDetailsService = customUserDetailsService;
//    }
}
