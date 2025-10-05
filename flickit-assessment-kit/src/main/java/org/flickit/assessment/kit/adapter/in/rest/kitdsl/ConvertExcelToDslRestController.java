package org.flickit.assessment.kit.adapter.in.rest.kitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitdsl.ConvertExcelToDslUseCase;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ConvertExcelToDslRestController {

    private final ConvertExcelToDslUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessment-kits/excel-to-dsl")
    ResponseEntity<byte[]> convertExcelToDsl(@RequestParam MultipartFile excelFile) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.convertExcelToDsl(toParam(excelFile, currentUserId));

        HttpHeaders headers = createHttpHeaders(result.fileName());
        return new ResponseEntity<>(result.file(), headers, HttpStatus.OK);
    }

    private ConvertExcelToDslUseCase.Param toParam(MultipartFile excelFile, UUID currentUserId) {
        return new ConvertExcelToDslUseCase.Param(excelFile, currentUserId);
    }

    private static HttpHeaders createHttpHeaders(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        ContentDisposition contentDisposition = ContentDisposition
            .attachment()
            .filename(fileName)
            .build();
        headers.setContentDisposition(contentDisposition);
        return headers;
    }
}
