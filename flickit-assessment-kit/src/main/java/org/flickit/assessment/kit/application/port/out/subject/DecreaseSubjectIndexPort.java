package org.flickit.assessment.kit.application.port.out.subject;

public interface DecreaseSubjectIndexPort {

    /***
     * @param kitVersionId kitVersionId of subjects
     * @param fromIndex low index (inclusive) of the subjects
     * @param toIndex high index (exclusive) of the subjects
     */
    void decreaseSubjectsIndexes(long kitVersionId, int fromIndex, int toIndex);
}
