package org.flickit.assessment.core.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.subject.ReportSubjectUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReportSubjectRestController {

    private final ReportSubjectUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/report/subjects/{subjectId}")
    public ResponseEntity<SubjectReportResponseDto> reportSubject(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("subjectId") Long subjectId) {
        UUID currentUserId = userContext.getUser().id();
        var param = toParam(assessmentId, currentUserId, subjectId);
        var result = useCase.reportSubject(param);
        var response = toResponse(result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ReportSubjectUseCase.Param toParam(UUID assessmentId, UUID currentUserId, Long subjectId) {
        return new ReportSubjectUseCase.Param(assessmentId, currentUserId, subjectId);
    }

    private SubjectReportResponseDto toResponse(ReportSubjectUseCase.Result result) {
        return new SubjectReportResponseDto(result.subject(),
            result.topStrengths(),
            result.topWeaknesses(),
            result.attributes(),
            result.maturityLevelsCount());
    }
}
