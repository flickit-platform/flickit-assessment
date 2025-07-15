package org.flickit.assessment.core.adapter.in.rest.questionnaire;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Builder;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentNextQuestionnaireUseCase;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetAssessmentNextQuestionnaireResponseDto(ResultStatus status, Data data) {

    public static GetAssessmentNextQuestionnaireResponseDto of(GetAssessmentNextQuestionnaireUseCase.Result result) {
        var status = switch (result) {
            case GetAssessmentNextQuestionnaireUseCase.Result.Found x -> ResultStatus.FOUND;
            case GetAssessmentNextQuestionnaireUseCase.Result.NotFound ignored -> ResultStatus.NOT_FOUND;
        };
        return GetAssessmentNextQuestionnaireResponseDto.builder()
            .status(status)
            .data(Data.of(result))
            .build();
    }

    sealed interface Data permits Data.Found {

        @Nullable
        static Data of(GetAssessmentNextQuestionnaireUseCase.Result result) {
            return switch (result) {
                case GetAssessmentNextQuestionnaireUseCase.Result.Found x -> Found.of(x);
                case GetAssessmentNextQuestionnaireUseCase.Result.NotFound ignored -> null;
            };
        }

        @Builder
        record Found(long id, long index, String title) implements Data {

            public static Found of(GetAssessmentNextQuestionnaireUseCase.Result.Found data) {
                return Found.builder()
                    .id(data.id())
                    .index(data.index())
                    .title(data.title())
                    .build();
            }
        }
    }

    public enum ResultStatus {
        FOUND, NOT_FOUND
    }
}
