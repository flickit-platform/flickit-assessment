package org.flickit.flickitassessmentcore.adapter.in.rest.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnsweredQuestionsCountUseCase.Progress;

import java.util.UUID;

record GetAnsweredQuestionsCountResponseDto(Progress<UUID> assessmentProgress) {
}
