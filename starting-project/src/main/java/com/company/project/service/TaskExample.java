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

import java.util.Date;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Romer
 */
@Component
@Scope("prototype")
public class TaskExample implements Runnable {
    String name;
 
    public void setName(String name){
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("Before :: " + name + " is running. Current time is :: "+ new Date());

        try {
                Thread.sleep(5000);
        } catch (InterruptedException e) {
                e.printStackTrace();
        }

        System.out.println("After :: " + name + " is running. Current time is :: "+ new Date());
    }
}
