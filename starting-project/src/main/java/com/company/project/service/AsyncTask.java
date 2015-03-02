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
import org.springframework.scheduling.annotation.Async;

/**
 *
 * @author Romer
 */

public class AsyncTask {
    @Async
    public void doAsyncTask(){
        System.out.println("do some async task. Current time is :: "+ new Date());
    }
}
