/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.exception;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Custom exception class ot be thrown on system errors this is the most generic
 * exception possible on oaf (Non Runtime Exception)
 *
 * @author Marco Weiland
 * @author Rafael Fernandes
 */
public class SystemException extends Exception {

	private static final long serialVersionUID = 1035008280958092725L;

	private final Set<String> messages = new HashSet<>();

	/**
	 * System Exception empty constructor
	 */
	public SystemException() {
		super();
	}

	/**
	 * System Exception constructor by a given exception
	 * @param e to be retrieved message
	 */
	public SystemException(Exception e) {
		super();
		messages.add(getMessage(e));
	}

	/**
	 * System Exception constructor by a given message
	 * @param message to create the system exception with
	 */
	public SystemException(String message) {
		super();
		messages.add(message);
	}

	/**
	 * System Exception Constructor by a given message and exception
	 * @param message to be added into the system exception
	 * @param e exception to be retrieved the message to be added into the system exception
	 */
	public SystemException(String message, Exception e) {
		super();
		messages.add(message);
		messages.add(getMessage(e));
	}

	/**
	 * Retrieves the message from the given exception
	 * @param e to be get the message
	 * @return a string message
	 */
	private String getMessage(Exception e) {

		return "wrapped exception: " + e.getClass().getCanonicalName() + " with message: " + e.getLocalizedMessage();
	}

	/**
	 * Gets multiple messages into a list of messages
	 * @return gathers all the messages and stores it into a list of messages
	 */
	public List<String> getMessages() {
		return new ArrayList<>(this.messages);
	}

	/**
	 * Adds a new string value into the message
	 * @param message to be added
	 */
	public void addMessage(String message) {
		messages.add(message);
	}

	/**
	 * Retrieves the messages from the system exception
	 * @return a string with all the existent messages
	 */
	@Override
	public String getMessage() {
		StringBuilder messageString = new StringBuilder("Message(s): ");

		if (messages.size() == 1) {
			return messageString + messages.iterator().next();
		}
		for (String message : messages) {
			messageString.append("[").append(message).append("]");
		}

		return messageString.toString();
	}
}
