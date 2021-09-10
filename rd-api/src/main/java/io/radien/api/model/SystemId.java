/*

	Copyright (c) 2021-present radien GmbH. All rights reserved.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

 */
package io.radien.api.model;

import java.io.Serializable;

/**
 * Interface that defines contract to retrieve
 * a basic id
 */
public interface SystemId extends Serializable {
    /**
     * Model id getter
     * @return model id
     */
    Long getId();

    /**
     * Model id setter
     * @param id to be set
     */
    void setId(Long id);
}
