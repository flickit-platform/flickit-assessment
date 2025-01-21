package org.flickit.assessment.core.adapter.in.rest.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAssessmentReportMetadataRestController {

    private final CreateAssessmentReportMetadataUseCase useCase;
    private final UserContext userContext;

    @PatchMapping("/assessments/{assessmentId}/report-metadata")
    ResponseEntity<Void> createReportMetadata(@PathVariable("assessmentId") UUID assessmentId,
                                              @RequestBody CreateAssessmentReportMetadataRequestDto request) {
        var currentUserId = userContext.getUser().id();
        useCase.createReportMetadata(toParam(assessmentId, request, currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, CreateAssessmentReportMetadataRequestDto request, UUID currentUserId) {
        var metadataParam = new CreateAssessmentReportMetadataUseCase.MetadataParam(request.intro(),
            request.prosAnsCons(),
            request.steps(),
            request.participants());
        return new Param(assessmentId, metadataParam, currentUserId);
    }
}
