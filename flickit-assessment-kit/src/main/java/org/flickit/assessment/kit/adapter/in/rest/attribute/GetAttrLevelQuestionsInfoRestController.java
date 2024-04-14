package org.flickit.assessment.kit.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttrLevelQuestionsInfoUseCase;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttrLevelQuestionsInfoUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAttrLevelQuestionsInfoRestController {

    private final GetAttrLevelQuestionsInfoUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessment-kits/{kitId}/details/attributes/{attributeId}/maturity-levels/{maturityLevelId}")
    public ResponseEntity<GetAttrLevelQuestionsInfoResponseDto> getAttributeLevelQuestions(
        @PathVariable("kitId") Long kitId,
        @PathVariable("attributeId") Long attributeId,
        @PathVariable("maturityLevelId") Long maturityLevelId) {

        UUID currentUserId = userContext.getUser().id();
        var param = toParam(kitId, attributeId, maturityLevelId, currentUserId);
        var attrLevelQuestionsInfo = useCase.getAttrLevelQuestionsInfo(param);
        var responseDto = toResponseDto(attrLevelQuestionsInfo);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetAttrLevelQuestionsInfoUseCase.Param toParam(Long kitId,
                                                           Long attributeId,
                                                           Long maturityLevelId,
                                                           UUID currentUserId) {

        return new GetAttrLevelQuestionsInfoUseCase.Param(kitId,
            attributeId,
            maturityLevelId,
            currentUserId);
    }

    private GetAttrLevelQuestionsInfoResponseDto toResponseDto(Result attrLevelQuestionsInfo) {
        return new GetAttrLevelQuestionsInfoResponseDto(attrLevelQuestionsInfo.id(),
            attrLevelQuestionsInfo.title(),
            attrLevelQuestionsInfo.index(),
            attrLevelQuestionsInfo.questionsCount(),
            attrLevelQuestionsInfo.questions());
    }
}
