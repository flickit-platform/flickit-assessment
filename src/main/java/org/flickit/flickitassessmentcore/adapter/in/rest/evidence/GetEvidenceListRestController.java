package org.flickit.flickitassessmentcore.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class GetEvidenceListRestController {

    private final GetEvidenceListUseCase useCase;

    @GetMapping
    @RequestMapping("/{spaceId}/{assessmentId}/{questionId}/evidences")
    public ResponseEntity<GetEvidenceListResponseDto> getEvidenceList(@PathVariable("questionId") Long questionId) {
        GetEvidenceListUseCase.Result result = useCase.getEvidenceList(new GetEvidenceListUseCase.Param(questionId));
        return new ResponseEntity<>(new GetEvidenceListResponseDto(result.evidences()), HttpStatus.OK);
    }
}
