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

package com.company.project.data.elasticsearch.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import static org.springframework.data.elasticsearch.annotations.FieldIndex.analyzed;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 *
 * @author Romer
 */
@Document( indexName = "parent-child",
        type = "parent-entity",
        indexStoreType = "mmapfs",
        shards = 1, replicas = 0,
        refreshInterval = "-1"
)
public class ParentEntity {
    
    public static final String INDEX = "parent-child";
    public static final String PARENT_TYPE = "parent-entity";
    public static final String CHILD_TYPE = "child-entity";

        
    @Id
    private String id;
    
    //@Field(type=FieldType.String)
    //@Field(type=FieldType.String)
    //@Field(type = FieldType.String, index = analyzed, store = true)
    //@Field(type = FieldType.String, store = true)
    @Field(type = FieldType.String, index = analyzed, store = true)
    private String name;
    
    private String path;
    
    @Field(type = FieldType.String, index = analyzed, store = true)
    private String description;
    
    private Long role;
    
    @Field(type = FieldType.Nested)
    private Map<Integer, Collection<String>> filter = new HashMap();
    
    public ParentEntity() {}
    
    public ParentEntity(String id, String name) {
      this.id = id;
      this.name = name;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the filter
     */
    public Map<Integer, Collection<String>> getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(Map<Integer, Collection<String>> filter) {
        this.filter = filter;
    }

    /**
     * @return the role
     */
    public Long getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(Long role) {
        this.role = role;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
}
