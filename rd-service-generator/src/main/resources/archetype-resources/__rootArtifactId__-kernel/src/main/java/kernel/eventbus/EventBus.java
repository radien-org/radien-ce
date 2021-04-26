/*
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
package ${package}.kernel.eventbus;

import ${package}.api.Event;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * THe Openappframe EventBus
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public @RequestScoped class EventBus implements Serializable {

	protected static final Logger log = LoggerFactory.getLogger(EventBus.class);
	private static final long serialVersionUID = 6812608123262000012L;

	/**
	 * Fires an {@link Event} in the current CDI bean manager
	 *
	 * @param event
	 *                  the event to be fired
	 */
	public void fireEvent(Event event) {
		BeanManager beanManager = CDI.current().getBeanManager();
		if (beanManager != null) {
			log.info("EventBus fired: {}", event.getClass().getSimpleName());
			beanManager.fireEvent(event);
		} else {
			log.info("BeanManager is null. event {} could not be fired", event);
		}
	}

}
