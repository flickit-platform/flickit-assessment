package org.flickit.assessment.kit.adapter.in.rest.kitlanguage;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitlanguage.AddLanguageToKitUseCase;
import org.flickit.assessment.kit.application.port.in.kitlanguage.AddLanguageToKitUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AddLanguageToKitRestController {

    private final AddLanguageToKitUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessment-kits/{kitId}/add-language")
    public ResponseEntity<Void> addLanguageToKit(@PathVariable long kitId,
                                                 @RequestBody AddLanguageToKitRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.addLanguageToKit(toParam(kitId, requestDto.lang(), currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(long kitId, String lang, UUID currentUserId) {
        return new Param(kitId,
            lang,
            currentUserId);
    }
}
