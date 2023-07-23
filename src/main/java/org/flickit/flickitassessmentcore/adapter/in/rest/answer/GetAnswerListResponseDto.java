package org.flickit.flickitassessmentcore.adapter.in.rest.answer;

import org.flickit.flickitassessmentcore.domain.Answer;

import java.util.List;

public record GetAnswerListResponseDto(List<Answer> answers) {
}
