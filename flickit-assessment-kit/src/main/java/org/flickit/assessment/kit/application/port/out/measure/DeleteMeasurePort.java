package org.flickit.assessment.kit.application.port.out.measure;

public interface DeleteMeasurePort {

    void delete(long measureId, long kitVersionId);
}
