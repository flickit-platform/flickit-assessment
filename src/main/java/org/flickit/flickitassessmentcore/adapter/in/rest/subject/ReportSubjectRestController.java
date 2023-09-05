package org.flickit.flickitassessmentcore.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport;
import org.flickit.flickitassessmentcore.application.port.in.subject.ReportSubjectUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportSubjectRestController {

    private final ReportSubjectUseCase useCase;

    @GetMapping("/subjects/{subjectId}/report")
    public ResponseEntity<SubjectReport> reportSubject(@PathVariable("subjectId") Long subjectId) {
        var param = toParam(subjectId);
        var result = useCase.reportSubject(param);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private ReportSubjectUseCase.Param toParam(Long subjectId) {
        return new ReportSubjectUseCase.Param(subjectId);
    }
}
