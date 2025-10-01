package org.flickit.assessment.kit.application.service.kitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.kitdsl.ConvertExcelToDslUseCase;
import org.flickit.assessment.kit.application.port.out.kitdsl.ConvertExcelToDslModelPort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_SIZE_MAX;

@Service
@RequiredArgsConstructor
public class ConvertExcelToDslService implements ConvertExcelToDslUseCase {

    private final FileProperties fileProperties;
    private final ConvertExcelToDslModelPort convertExcelToDslModelPort;

    @Override
    public AssessmentKitDslModel convertExcelToDsl(Param param) {
        validateFile(param.getExcelFile());
        return convertExcelToDslModelPort.convert(param.getExcelFile());
    }

    private void validateFile(MultipartFile excelFile) {
        if (excelFile.getSize() >= fileProperties.getExcelKitMaxSize().toBytes())
            throw new ValidationException(UPLOAD_FILE_SIZE_MAX);

        if (!fileProperties.getExcelKitContentType().equals(excelFile.getContentType()))
            throw new ValidationException(UPLOAD_FILE_FORMAT_NOT_VALID);
    }
}
