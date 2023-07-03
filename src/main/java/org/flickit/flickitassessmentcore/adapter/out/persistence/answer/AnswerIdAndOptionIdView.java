package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;


import java.util.UUID;

interface AnswerIdAndOptionIdView {

    UUID getId();

    Long getAnswerOptionId();

    Boolean getIsApplicable();
}
