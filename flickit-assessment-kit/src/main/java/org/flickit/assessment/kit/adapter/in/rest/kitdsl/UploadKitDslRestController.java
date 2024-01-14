package org.flickit.assessment.kit.adapter.in.rest.kitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitdsl.UploadKitDslUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UploadKitDslRestController {

    private final UploadKitDslUseCase useCase;
    private final UserContext userContext;

    @PostMapping("assessment-kits/upload-dsl")
    public ResponseEntity<UploadKitResponseDto> uploadDsl(@RequestParam("dslFile") MultipartFile dslFile,
                                                       @RequestParam("expertGroupId") Long expertGroupId) {
        UUID currentUserId = userContext.getUser().id();
        Long kitDslId = useCase.upload(toParam(dslFile, expertGroupId, currentUserId));
        return new ResponseEntity<>(toResponse(kitDslId), HttpStatus.OK);
    }

    private UploadKitDslUseCase.Param toParam(MultipartFile dslFile, Long expertGroupId, UUID currentUserId) {
        return new UploadKitDslUseCase.Param(dslFile, expertGroupId, currentUserId);
    }

    private UploadKitResponseDto toResponse(Long kitDslId) {
        return new UploadKitResponseDto(kitDslId);
    }
}
