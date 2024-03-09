package org.flickit.assessment.core.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.report.SubjectReport;
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

    @GetMapping("/assessments/{assessmentId}/report/subjects/{subjectRefNum}")
    public ResponseEntity<SubjectReport> reportSubject(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("subjectRefNum") UUID subjectRefNum) {
        var param = toParam(assessmentId, subjectRefNum);
        var result = useCase.reportSubject(param);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private ReportSubjectUseCase.Param toParam(UUID assessmentId, UUID subjectRefNum) {
        return new ReportSubjectUseCase.Param(assessmentId, subjectRefNum);
    }
}
