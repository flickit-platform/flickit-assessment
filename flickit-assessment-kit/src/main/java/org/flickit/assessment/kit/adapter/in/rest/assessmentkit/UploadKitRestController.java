package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UploadKitUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UploadKitRestController {

    private final UploadKitUseCase useCase;

    @PostMapping("assessment-kits/upload")
    public ResponseEntity<UploadKitResponseDto> upload(@RequestParam("dslFile") MultipartFile dslFile) {
        var result = useCase.upload(toParam(dslFile));
        return new ResponseEntity<>(toResponse(result), HttpStatus.OK);
    }

    private UploadKitUseCase.Param toParam(MultipartFile dslFile) {
        return new UploadKitUseCase.Param(dslFile);
    }

    private UploadKitResponseDto toResponse(UploadKitUseCase.Result result) {
        return new UploadKitResponseDto(result.kitDslId(), result.syntaxError());
    }
}
