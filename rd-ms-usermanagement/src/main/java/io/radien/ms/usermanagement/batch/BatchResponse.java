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

package io.radien.ms.usermanagement.batch;

import io.radien.api.service.batch.BatchSummary;

import javax.ws.rs.core.Response;

/**
 * User batch response processing response
 *
 * @author Bruno Gama
 */
public class BatchResponse {

    /**
     * Converts into a response able to be sent to the user the batch summary
     * @param summary of the batch processing
     * @return the response with the summary
     */
    public static Response get(BatchSummary summary) {
        if (summary.getTotalNonProcessed() > 0 && summary.getTotalProcessed() == 0) {
                // None users were inserted
                return Response.status(Response.Status.BAD_REQUEST).entity(summary).build();
        }
        // All (or some) users were inserted
        return Response.ok().entity(summary).build();
    }
}