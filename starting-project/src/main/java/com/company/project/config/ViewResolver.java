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

package com.company.project.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 *
 * @author Romer
 */
@Component
public class ViewResolver extends InternalResourceViewResolver {
    /**
	 * Use '/WEB-INF/views/' for the prefix and '.jsp' for the suffix.
	 */
	public ViewResolver() {
            super();
            setViewClass(JstlView.class);
            setPrefix("/WEB-INF/pages/");
            setSuffix(".jsp");
	}

//	@Override
//	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
//		if (viewName.isEmpty() || viewName.endsWith("/")) {
//			viewName += "index";
//		}
//		return super.buildView(viewName);
//	}
}
