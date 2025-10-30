package org.flickit.assessment.data.jpa.core.evidence;

public interface QuestionEvidenceCommentCountView {

    long getQuestionId();

    int getEvidenceCount();

    int getCommentCount();
}
