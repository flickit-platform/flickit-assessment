package org.flickit.assessment.kit.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.attribute.GetKitAttributeLevelQuestionsDetailUseCase;
import org.flickit.assessment.kit.application.port.in.attribute.GetKitAttributeLevelQuestionsDetailUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitAttributeLevelQuestionsDetailRestController {

    private final GetKitAttributeLevelQuestionsDetailUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessment-kits/{kitId}/details/attributes/{attributeId}/maturity-levels/{maturityLevelId}")
    public ResponseEntity<GetKitAttributeLevelQuestionsDetailResponseDto> getAttributeLevelQuestionsDetail(
        @PathVariable("kitId") Long kitId,
        @PathVariable("attributeId") Long attributeId,
        @PathVariable("maturityLevelId") Long maturityLevelId) {

        UUID currentUserId = userContext.getUser().id();
        var param = toParam(kitId, attributeId, maturityLevelId, currentUserId);
        var attrLevelQuestionsInfo = useCase.getKitAttributeLevelQuestionsDetail(param);
        var responseDto = toResponseDto(attrLevelQuestionsInfo);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetKitAttributeLevelQuestionsDetailUseCase.Param toParam(Long kitId,
                                                                     Long attributeId,
                                                                     Long maturityLevelId,
                                                                     UUID currentUserId) {
        return new GetKitAttributeLevelQuestionsDetailUseCase.Param(kitId,
            attributeId,
            maturityLevelId,
            currentUserId);
    }

    private GetKitAttributeLevelQuestionsDetailResponseDto toResponseDto(Result attrLevelQuestionsInfo) {
        return new GetKitAttributeLevelQuestionsDetailResponseDto(
            attrLevelQuestionsInfo.questionsCount(),
            attrLevelQuestionsInfo.questions());
    }
}
