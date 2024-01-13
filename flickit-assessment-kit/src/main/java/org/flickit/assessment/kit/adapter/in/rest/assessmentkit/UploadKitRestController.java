package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.adapter.out.uploaddsl.exception.DSLHasSyntaxErrorException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UploadKitUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UploadKitRestController {

    private final UploadKitUseCase useCase;
    private final UserContext userContext;

    @PostMapping("assessment-kits/upload")
    public ResponseEntity<UploadKitResponseDto> upload(@RequestParam("dslFile") MultipartFile dslFile,
                                                       @RequestParam("expertGroupId") Long expertGroupId) {
        UUID currentUserId = userContext.getUser().id();
        try {
            Long kitDslId = useCase.upload(toParam(dslFile, expertGroupId, currentUserId));
            return new ResponseEntity<>(toResponse(kitDslId, null), HttpStatus.OK);
        } catch (DSLHasSyntaxErrorException e) {
            return new ResponseEntity<>(toResponse(null, e.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private UploadKitUseCase.Param toParam(MultipartFile dslFile, Long expertGroupId, UUID currentUserId) {
        return new UploadKitUseCase.Param(dslFile, expertGroupId, currentUserId);
    }

    private UploadKitResponseDto toResponse(Long kitDslId, String dslError) {
        return new UploadKitResponseDto(kitDslId, dslError);
    }
}
