/**
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ${package}.kernel.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
@Interceptor
@RuntimeIntercepted
public class MethodRuntimeInterceptor {
	private static final long serialVersionUID = 6812608123262000013L;
	private static final Logger log = LoggerFactory.getLogger(MethodRuntimeInterceptor.class);
	private static long counter = 0;

	@AroundInvoke
	public Object around(InvocationContext ctx) {
		Object result = null;
		try {
			counter++;
			long start = 0;

			start = System.currentTimeMillis();
			log.info("START ID: " + counter + "|Class: |" + ctx.getMethod().getDeclaringClass().getName()
					+ "| Method: |" + ctx.getMethod().getName());

			result = ctx.proceed();

			long end = System.currentTimeMillis();
			long duration = end - start;
			log.info("END ID: " + counter + "|Class: |" + ctx.getMethod().getDeclaringClass().getName() + "| Method: |"
					+ ctx.getMethod().getName() + "| Milliseconds: |" + duration + "| Result: |" + result);

		} catch (Exception e) {
			log.error("exception during method interception: " + e);
		}
		return result;
	}
}
