/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.kernel.interceptor;

import io.radien.exception.GenericErrorCodeMessage;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Method Runtime Interceptor constructor object class
 *
 * @author Marco Weiland
 */
@Interceptor
@RuntimeIntercepted
public class MethodRuntimeInterceptor {
	private static final Logger log = LoggerFactory.getLogger(MethodRuntimeInterceptor.class);
	private static long counter = 0;


	/**
	 * Method to intercept invocations and life-cycle events on an associated target class.
	 * In life-cycle events such as methods that create/destroy the bean occur, or an
	 * EJB timeout method occurs.
	 * @param ctx invocation context for intercept the runtime methods
	 * @return the context result
	 */
	@AroundInvoke
	public Object around(InvocationContext ctx) {
		Object result = null;
		try {
			counter++;
			long start = 0;

			start = System.currentTimeMillis();
			String infoSystemStart = GenericErrorCodeMessage.INFO_SYSTEM_START.toString(String.valueOf(counter));
			log.info(infoSystemStart, ctx.getMethod().getDeclaringClass().getName(), ctx.getMethod().getName());

			result = ctx.proceed();

			long end = System.currentTimeMillis();
			long duration = end - start;
			String infoSystemEnd = GenericErrorCodeMessage.INFO_SYSTEM_END.toString(String.valueOf(counter));
			log.info(infoSystemEnd,
					ctx.getMethod().getDeclaringClass().getName(), ctx.getMethod().getName(), duration,
					result);

		} catch (Exception e) {
			log.error(GenericErrorCodeMessage.ERROR_METHOD_INTERCEPTION.toString(), e);
		}
		return result;
	}
}
