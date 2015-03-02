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

import com.company.project.persistence.UserMapper;
import com.company.project.service.IUserService;
import com.company.project.service.UserService;
import com.company.project.service.UserMapperImpl;
import com.company.project.service.dao.UserDao;
import java.lang.annotation.Annotation;
import java.security.Permission;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
//import javax.persistence.PersistenceContext;

/**
 *
 * @author Romer
 */
//@Service("customUserDetailsService")
@Service
@Transactional(readOnly=true)
public class CustomUserDetailsService implements UserDetailsService {
    
    private Map<String, Map<String, String>> users = new HashMap<>();
    
    @Autowired
    @Qualifier("userService")
    private IUserService userService;    // using spring-mybatis
    
    @Autowired
    private UserDao userDao;            // using jdbc template
    
    @Autowired
    @Qualifier("userMapperImpl")
    //@PersistenceContext(unitName = "osm.entityManagerFactory")
    private UserMapperImpl userMapperImpl;  // Using the MyBatis API
    
    public CustomUserDetailsService(){}
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
//            Map<String, String> user = new HashMap<String, String>();
//            user.put("username", "admin");
//            user.put("password", "admin");
//            user.put("role", "ROLE_ADMIN");
//            users.put("admin", user);
//            user = new HashMap<String, String>();
//            user.put("username", "sadmin");
//            user.put("password", "sadmin");
//            user.put("role", "ROLE_SYS_ADMIN");
//            users.put("sadmin", user);
//            user = new HashMap<String, String>();
//            user.put("username", "user");
//            user.put("password", "user");
//            user.put("role", "ROLE_USER");
//            users.put("user", user);
//            
//            user = users.get(username);
            
            Map<String, Object> user = null;
            //user = userService.get(username);
            //user = userDao.get(username);
            user = userMapperImpl.get(username);
            if (user == null) {
                return null;
            }
            
            if (username.contains("admin")) {
                user.put("role", "ROLE_ADMIN");
            } else {
                user.put("role", "ROLE_USER");
            }
            
            
            
            List<GrantedAuthority> authorities = getAuthorities("" + user.get("role"));
            boolean enabled = true;
            boolean accountNonExpired = true;
            boolean credentialsNonExpired = true;
            boolean accountNonLocked = true;

            // BCryptPasswordEncoder automatically generates a salt and concatenates it.
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode("" + user.get("password"));
            
            return new User(
                    "" + user.get("username"),
                    encodedPassword,
                    enabled,
                    accountNonExpired,
                    credentialsNonExpired,
                    accountNonLocked,
                    authorities);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        
//        for (Role role : employee.getRoles()) {
//            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(role));
//            }
//        }
        return authorities;
    }
}
