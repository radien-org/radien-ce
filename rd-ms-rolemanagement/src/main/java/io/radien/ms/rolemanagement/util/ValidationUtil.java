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
package io.radien.ms.rolemanagement.util;

import io.radien.exception.InvalidArgumentException;

/**
 * Abstract class that covers the common features/methods to be used
 * by TenantRole, TenantRoleUser and TenantRolePermission
 */
public class ValidationUtil {
    private ValidationUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Check if all specified parameters were informed
     * @param params vars contains variables that correspond method parameters
     */
    public static void checkIfMandatoryParametersWereInformed(Object...params) throws InvalidArgumentException {
        for (Object o:params) {
            if (o == null)
                throw new InvalidArgumentException("Mandatory parameter missing");
        }
    }
}
