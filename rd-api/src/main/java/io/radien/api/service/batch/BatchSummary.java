package io.radien.api.service.batch;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import java.util.ArrayList;
import java.util.Collection;

@JsonbPropertyOrder(value = {"total", "processed", "not-processed", "status", "issues"})
public class BatchSummary {

    public static enum ProcessingStatus {
        SUCCESS, PARTIAL_SUCCESS, FAIL
    }

    @JsonbProperty("total")
    private int total;

    @JsonbProperty("issues")
    private Collection<DataIssue> nonProcessedItems;

    public BatchSummary(int total) {
        this.total = total;
        this.nonProcessedItems = new ArrayList<>();
    }

    public BatchSummary(int total, Collection<DataIssue> nonProcessedItems) {
        if (nonProcessedItems == null)
            throw new IllegalArgumentException("nonProcessedItems cannot be null");
        this.total = total;
        this.nonProcessedItems = nonProcessedItems;
    }

    public void addNonProcessedItem(DataIssue dataIssue) {
        nonProcessedItems.add(dataIssue);
    }

    public void addNonProcessedItems(Collection<DataIssue> dataIssues) {
        nonProcessedItems.addAll(dataIssues);
    }

    @JsonbProperty("processed")
    public int getTotalProcessed() {
        return total - nonProcessedItems.size();
    }

    @JsonbProperty("not-processed")
    public int getTotalNonProcessed() {
        return nonProcessedItems.size();
    }

    public Collection<DataIssue> getNonProcessedItems() {
        return nonProcessedItems;
    }

    public int getTotal() {
        return total;
    }

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
