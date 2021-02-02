package io.radien.ms.usermanagement.batch;

import io.radien.api.service.batch.BatchSummary;

import javax.ws.rs.core.Response;

public class BatchResponse {
    public static Response get(BatchSummary summary) {
        if (summary.getTotalNonProcessed() > 0) {
            if (summary.getTotalProcessed() == 0) {
                // None users were inserted
                return Response.status(Response.Status.BAD_REQUEST).entity(summary).build();
            }
        }
        // All (or some) users were inserted
        return Response.ok().entity(summary).build();
    }
}