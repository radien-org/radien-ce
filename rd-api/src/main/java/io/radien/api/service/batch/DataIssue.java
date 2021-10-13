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

package io.radien.api.service.batch;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonbPropertyOrder({"row", "reasons"})
public class DataIssue {

    @JsonbProperty("row")
    private long rowId;
    private java.util.List<String> reasons;

    /**
     * Empty constructor
     */
    private DataIssue(){}

    /**
     * Data issues (process items) to be used in the batch summary
     * @param rowId of the item
     * @param reasons in case of multiple failure
     */
    public DataIssue(long rowId, java.util.List<String> reasons) {
        if (reasons == null)
            throw  new IllegalArgumentException("Reasons cannot be null");
        this.rowId = rowId;
        this.reasons = reasons;
    }

    /**
     * Data issues (process items) to be used in the batch summary
     * @param rowId of the item
     * @param reason in case of failure
     */
    public DataIssue(long rowId, String reason) {
        if (reason == null)
            throw new IllegalArgumentException("Reason cannot be null");
        this.rowId = rowId;
        this.reasons = new ArrayList<>();
        this.reasons.add(reason);
    }

    /**
     * Processed item row id
     * @return row id
     */
    public long getRowId() {
        return rowId;
    }

    /**
     * Getter for the list of multiple reasons why the processes are having issues
     * @return a list of motives of issues
     */
    public List<String> getReasons() {
        return reasons;
    }

    /**
     * Adds reason to the list of reasons
     * @param reason to be added
     */
    public void addReason(String reason) {
        reasons.add(reason);
    }
}
