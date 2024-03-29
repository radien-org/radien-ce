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
package io.radien.api.service.tenant.exception;

import io.radien.exception.NotFoundException;

/**
 * Exception to express inconsistencies regarding Active Tenant domain object (Not Found)
 *
 * @author Bruno Gama
 */
public class TenantNotFoundException extends NotFoundException {
    /**
     * Active Tenant exception constructor by a given message
     * @param message to create the tenant exception with
     */
    public TenantNotFoundException(String message) {
        super(message);
    }
}
