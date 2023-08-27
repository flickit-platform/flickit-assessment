package org.flickit.flickitassessmentcore.adapter.in.rest.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase.AnswerItem;

import java.util.List;

public record GetAnswerListResponseDto(List<AnswerItem> answers) {
}
