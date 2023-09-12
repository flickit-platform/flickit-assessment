package org.flickit.flickitassessmentcore.adapter.in.rest.subject;

import java.util.UUID;

record GetSubjectAnsweredQuestionsCountResponseDto (UUID id, Integer answerCount, Integer questionCount) {
}
