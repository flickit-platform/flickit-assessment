package org.flickit.assessment.kit.application.service.kitdsl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.adapter.out.serializer.MaturityLevelDslSerializer;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.flickit.assessment.kit.application.port.in.kitdsl.ConvertExcelToDslUseCase;
import org.flickit.assessment.kit.application.port.out.kitdsl.ConvertAssessmentKitDslModelPort;
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
    private final ConvertAssessmentKitDslModelPort convertAssessmentKitDslModelPort;

    @Override
    public AssessmentKitDslModel convertExcelToDsl(Param param) {
        validateFile(param.getExcelFile());
        var assessmentKitDslModel=  convertExcelToDslModelPort.convert(param.getExcelFile());
        var fileNameToContent = convertAssessmentKitDslModelPort.toDsl(assessmentKitDslModel);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(MaturityLevelDslModel.class, new MaturityLevelDslSerializer());
        mapper.registerModule(module);

        for (MaturityLevelDslModel level : assessmentKitDslModel.getMaturityLevels()) {
            String dsl = null;
            try {
                dsl = mapper.writeValueAsString(level);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            System.out.print(dsl);
        }

        return null;
    }

    private void validateFile(MultipartFile excelFile) {
        if (excelFile.getSize() >= fileProperties.getExcelKitMaxSize().toBytes())
            throw new ValidationException(UPLOAD_FILE_SIZE_MAX);

        if (!fileProperties.getExcelKitContentType().equals(excelFile.getContentType()))
            throw new ValidationException(UPLOAD_FILE_FORMAT_NOT_VALID);
    }
}
