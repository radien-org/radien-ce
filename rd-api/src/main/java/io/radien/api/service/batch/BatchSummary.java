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

package io.radien.api.service.batch;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import java.util.ArrayList;
import java.util.Collection;

@JsonbPropertyOrder(value = {"total", "processed", "not-processed", "status", "issues"})
public class BatchSummary {

    /**
     * Batch processing status inner enum
     */
    public static enum ProcessingStatus {
        SUCCESS, PARTIAL_SUCCESS, FAIL
    }

    @JsonbProperty("total")
    private int total;

    @JsonbProperty("issues")
    private Collection<DataIssue> nonProcessedItems;

    /**
     * Batch summary constructor
     * @param total records in the batch
     */
    public BatchSummary(int total) {
        this.total = total;
        this.nonProcessedItems = new ArrayList<>();
    }

    /**
     * Batch summary constructor
     * @param total records in the batch
     * @param nonProcessedItems collection list of unprocessed items
     */
    public BatchSummary(int total, Collection<DataIssue> nonProcessedItems) {
        if (nonProcessedItems == null)
            throw new IllegalArgumentException("nonProcessedItems cannot be null");
        this.total = total;
        this.nonProcessedItems = nonProcessedItems;
    }

    /**
     * Adds the non process item into a collection
     * @param dataIssue non process item
     */
    public void addNonProcessedItem(DataIssue dataIssue) {
        nonProcessedItems.add(dataIssue);
    }

    /**
     * Adds the non process item into a collection
     * @param dataIssues all the non process item
     */
    public void addNonProcessedItems(Collection<DataIssue> dataIssues) {
        nonProcessedItems.addAll(dataIssues);
    }

    /**
     * Counts all the processed items
     * @return the count of all the processed items
     */
    @JsonbProperty("processed")
    public int getTotalProcessed() {
        return total - nonProcessedItems.size();
    }

    /**
     * Counts all the non processed items
     * @return the count of all the non process items
     */
    @JsonbProperty("not-processed")
    public int getTotalNonProcessed() {
        return nonProcessedItems.size();
    }

    /**
     * Getter for the collection of non process items
     * @return a collection of data issues
     */
    public Collection<DataIssue> getNonProcessedItems() {
        return nonProcessedItems;
    }

    /**
     * Getter for the counter of all the items
     * @return how many items have been processed
     */
    public int getTotal() {
        return total;
    }

    /**
     * Gets the internal status of the batch processing
     * @return the internal status
     */
    @JsonbProperty("status")
    public ProcessingStatus getInternalStatus() {
        if (getTotalNonProcessed() > 0) {
            if (getTotalProcessed() == 0)
                return ProcessingStatus.FAIL;
            else
                return ProcessingStatus.PARTIAL_SUCCESS;
        }
        return ProcessingStatus.SUCCESS;
    }
}
