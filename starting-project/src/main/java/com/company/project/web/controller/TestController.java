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

package com.company.project.web.controller;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.WebAsyncTask;

/**
 *
 * @author Romer
 */
@Controller
@RequestMapping(value="/example")
public class TestController {
    
    private Gson gson = new Gson();
    
    @RequestMapping(value="/responseEntity", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    //@Secured("ROLE_USER")
    public ResponseEntity<String> responseEntityMap(final HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        
        String json = gson.toJson(map);

        return new ResponseEntity<>(json, HttpStatus.OK);
    }
    
    @RequestMapping(value="/responseEntityList", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> responseEntityMapList(final HttpServletRequest request) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        list.add(map);
        map = new HashMap<>();
        map.put("key1", "value4");
        map.put("key2", "value5");
        map.put("key3", "value6");
        list.add(map);
        
        String json = gson.toJson(list);

        return new ResponseEntity<>(json, HttpStatus.OK);
    }
    
    @RequestMapping(value="/responseEntityMapParam", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> responseEntityMapParam(
            @RequestParam(value = "paramKey", required = true) final String key,
            @RequestParam(value = "paramValue", required = true) final String value) {
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("key", key);
        map.put("value", value);

        return new ResponseEntity<>(gson.toJson(map), HttpStatus.OK);
    }
    
    @RequestMapping(value="/responseEntityMapParamAndPathVariable/{key}/{value}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> responseEntityMapParamAndPathVariable(
            @PathVariable("key") String keyPath,
            @PathVariable("value") String valuePath,
            @RequestParam(value = "paramKey", defaultValue="World", required = true) final String keyParam,
            @RequestParam(value = "paramValue", required = true) final String valueParam) {
        Map<String, String> map = new HashMap<>();
        map.put("keyPath", "" + keyPath);
        map.put("valuePath", "" + valuePath);
        map.put("keyParam", "" + keyParam);
        map.put("valueParam", "" + valueParam);
        
        return new ResponseEntity<String>(gson.toJson(map), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/webAsyncTask", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)  //, headers = "Accept=application/json", consumes="application/json", produces = { "application/json", "application/xml" }produces = "application/xml", headers = { "key1=val1", "key2=val2" },  produces="text/plain;charset=UTF-8"
    public @ResponseBody WebAsyncTask<String> webAsyncTask(
            @RequestParam(value = "paramKey", required = true) final String key,
            @RequestParam(value = "paramValue", required = true) final String value) {
        
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                
                Map<String, String> map = new HashMap<>();
                map.put("key", key);
                map.put("value", value);

                return gson.toJson(map);
            }
        };

        return new WebAsyncTask<>(5000, callable);
    }
    
    @RequestMapping(value = "/callable",
            method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Callable<String> callable(final HttpServletRequest request) {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                Map<String, String> map = new HashMap<>();
                map.put("key1", "value1");
                map.put("key2", "value2");
                map.put("key3", "value3");
                 
                return gson.toJson(map);
            }
        };
    }
}
