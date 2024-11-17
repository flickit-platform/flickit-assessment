package org.flickit.assessment.kit.adapter.in.rest.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.domain.KitCustomData;
import org.flickit.assessment.kit.application.port.in.kitcustom.CreateKitCustomUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateKitCustomRestController {

    private final CreateKitCustomUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessment-kits/{kitId}/kit-customs")
    public ResponseEntity<CreateKitCustomResponseDto> createKitCustom(@PathVariable("kitId") Long kitId,
                                                                      @RequestBody CreateKitCustomRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        long kitCustomId = useCase.createKitCustom(toParam(kitId, currentUserId, requestDto));
        return new ResponseEntity<>(new CreateKitCustomResponseDto(kitCustomId), HttpStatus.CREATED);
    }

    private static CreateKitCustomUseCase.Param toParam(long kitId,
                                                        UUID currentUserId,
                                                        CreateKitCustomRequestDto requestDto) {
        KitCustomData customData = null;
        var customDataDto = requestDto.customData();
        if (customDataDto != null) {
            var subjectDtos = customDataDto.subjects();
            if (subjectDtos == null)
                subjectDtos = new ArrayList<>();

            var attributeDtos = customDataDto.attributes();
            if (attributeDtos == null)
                attributeDtos = new ArrayList<>();

            var questionnaireDtos = customDataDto.questionnaires();
            if (questionnaireDtos == null)
                questionnaireDtos = new ArrayList<>();

            var subjects = subjectDtos.stream()
                .map(e -> new KitCustomData.Subject(e.id(), e.weight()))
                .toList();
            var attributes = attributeDtos.stream()
                .map(e -> new KitCustomData.Attribute(e.id(), e.weight()))
                .toList();
            var questionnaires = questionnaireDtos.stream()
                .map(e -> new KitCustomData.Questionnaire(e.id(), e.disabled()))
                .toList();

            customData = new KitCustomData(subjects, attributes, questionnaires);
        }

        return new CreateKitCustomUseCase.Param(kitId, requestDto.title(), customData, currentUserId);
    }
}
