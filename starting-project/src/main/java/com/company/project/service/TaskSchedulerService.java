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
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
 * @author Romer
 */
public class TaskSchedulerService {
    
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    

    // every 60 sec
    @Scheduled(fixedRate = 60000)
    public void schedulerFixedRate() {
        System.out.println("schedulerFixedRate. Current time is :: "+ new Date());
        
        // spring managed thread
        TaskExample taskExample = new TaskExample();
        taskExample.setName("Thread 1");
        taskExecutor.execute(taskExample);
        
        asyncMethod();
        
        taskExample = new TaskExample();
        taskExample.setName("Thread 2");
        taskExecutor.execute(taskExample);
        
        taskExample = new TaskExample();
        taskExample.setName("Thread 3");
        Future future = taskExecutor.submit(taskExample);
        System.out.println("Thread 3 future.isCancelled() :: " + future.isCancelled());
        System.out.println("Thread 3 future.isDone() :: " + future.isDone());
        while(!future.isDone()) {
            try {
                Thread.sleep(1000);
            } catch(Exception e) {}
        }
        System.out.println("Thread 3 future.isDone() :: " + future.isDone());       // future.isDone() = true
        
        AsyncTask asyncTask = new AsyncTask();
        asyncTask.doAsyncTask();
    }
    
    // every 70 sec
    @Scheduled(fixedDelay = 70000)
    public void schedulerFixedDelay() {
        System.out.println("schedulerFixedDelay. Current time is :: "+ new Date());
    }
    
    // run refresh job every day a 9am=0 0 9 * * *
    // "0 0 * * * *" = the top of every hour of every day.
    // "*/10 * * * * *" = every ten seconds.
    // "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
    // "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
    // "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
    // "0 0 0 25 12 ?" = every Christmas Day at midnight
    // "*/5 * * * * MON-FRI"
     // every 80 sec
    @Scheduled(cron="*/80 * * * * ?")
    public void schedulerCron() {
        System.out.println("schedulerCron. Current time is :: " + new Date());
    }
    
    @Async
    public void asyncMethod() {
        try {
            Thread.sleep(1000);
        } catch(Exception e) {}
        
        System.out.println("Async method. Current time is :: " + new Date());
    }
}
