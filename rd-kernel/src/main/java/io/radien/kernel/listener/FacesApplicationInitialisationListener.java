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
package io.radien.kernel.listener;

import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Faces Application Initialisation listener constructer class
 *
 * @author Marco Weiland
 */
// @WebListener
public class FacesApplicationInitialisationListener implements SystemEventListener {

	private static final Logger log = LoggerFactory.getLogger(FacesApplicationInitialisationListener.class);

	/**
	 * Processes the current requested event
	 * @param event to be processed
	 * @throws AbortProcessingException in case of triggered event to destroy the current requested event
	 */
	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {
		log.info("Process event triggered");
	}

	/**
	 * Validates if a current requested source is an instance of an application object
	 * @param source to be validated
	 * @return true in case the given source is an application
	 */
	@Override
	public boolean isListenerForSource(Object source) {
		return source instanceof Application;
	}
}
