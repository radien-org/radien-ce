package io.radien.api.service.batch;

import com.sun.org.apache.xerces.internal.xs.StringList;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonbPropertyOrder({"row", "reasons"})
public class DataIssue {

    @JsonbProperty("row")
    private long rowId;
    private java.util.List<String> reasons;

    public DataIssue(long rowId, java.util.List<String> reasons) {
        if (reasons == null)
            throw  new IllegalArgumentException("Reasons cannot be null");
        this.rowId = rowId;
        this.reasons = reasons;
    }

    public DataIssue(long rowId, String reason) {
        if (reason == null)
            throw new IllegalArgumentException("Reason cannot be null");
        this.rowId = rowId;
        this.reasons = new ArrayList<>();
        this.reasons.add(reason);
    }

    public long getRowId() {
        return rowId;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void addReason(String reason) {
        reasons.add(reason);
    }
}
