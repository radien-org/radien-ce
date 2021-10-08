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
package io.rd.exception;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Custom exception class ot be thrown on system errors this is the most generic
 * exception possible on oaf (Non Runtime Exception)
 *
 * @author Rajesh Gavvala
 * @author Marco Weiland
 * @author Rafael Fernandes
 */
public class SystemException extends Exception {

	private static final long serialVersionUID = 6812608123262000009L;

	private final Set<String> messages = new HashSet<>();

	public SystemException() {
		super();
	}

	public SystemException(Exception e) {
		super();
		messages.add(getMessage(e));
	}

	private String getMessage(Exception e) {

		return "wrapped exception: " + e.getClass().getCanonicalName() + " with message: " + e.getLocalizedMessage();
	}

	public SystemException(String message) {
		super();
		messages.add(message);
	}

	public SystemException(String message, Exception e) {
		super();
		messages.add(message);
		messages.add(getMessage(e));
	}

	public List<String> getMessages() {
		return new ArrayList<>(this.messages);
	}

	public void addMessage(String message) {
		messages.add(message);
	}

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
