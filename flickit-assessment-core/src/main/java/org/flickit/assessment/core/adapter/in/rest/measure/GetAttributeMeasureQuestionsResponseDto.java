package org.flickit.assessment.core.adapter.in.rest.measure;

import org.flickit.assessment.core.application.port.in.measure.GetAttributeMeasureQuestionsUseCase.Result;

import java.util.List;

public record GetAttributeMeasureQuestionsResponseDto(List<Result> items) {
}
