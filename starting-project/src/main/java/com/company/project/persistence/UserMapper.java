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

package com.company.project.persistence;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 *
 * @author Romer
 */
public interface UserMapper {
    Map get(String username);
    List getAll();
    int insert(Map user);
    int update(Map user);
    int delete(String username);
    
    //SQL query using annotation
    //@Select("SELECT * FROM person")
    //public List selectAllPerson2();
    //SQL query using annotation
    //@Select("SELECT * FROM person WHERE id = #{id}")
    //public Person selectPerson(@Param("id") int id);
    //@Insert("INSERT INTO person (name) VALUES (#{name})")
    //public int insertPerson(Person person);
}
