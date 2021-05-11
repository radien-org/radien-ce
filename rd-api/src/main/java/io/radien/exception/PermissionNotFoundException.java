/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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

/**
 * To be describe situations whereas an expected Permission could not be found
 *
 * @author Newton Carvalho
 */
public class PermissionNotFoundException extends Exception {
	private static final long serialVersionUID = 6529242860702854107L;

	/**
	 * Permission Not Found exception constructor by a given message
	 * @param message to create the permission not found exception with
	 */
	public PermissionNotFoundException(String message) {
		super(message);
	}
}
