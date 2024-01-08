package org.flickit.assessment.kit.application.service.assessmentkit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UploadKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.GetDslContentPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UploadKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UploadKitService implements UploadKitUseCase {

    private final UploadKitPort uploadKitPort;
    private final GetDslContentPort getDslContentPort;
    private final CreateAssessmentKitDslPort createAssessmentKitDslPort;

    @SneakyThrows
    @Override
    public UploadKitUseCase.Result upload(UploadKitUseCase.Param param) {
        AssessmentKitDslModel dslContentJson = getDslContentPort.getDslContent(param.getDslFile());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(dslContentJson);
        UploadKitPort.Result uploadedFilesInformation = uploadKitPort.upload(param.getDslFile(), json);
        CreateAssessmentKitDslPort.Result persistedDataIds = createAssessmentKitDslPort.create(toCreateAssessmentKitDslParam(uploadedFilesInformation));
        return toResult(persistedDataIds);
    }

    private Result toResult(CreateAssessmentKitDslPort.Result persistedDataIds) {
        return new UploadKitUseCase.Result(persistedDataIds.kitZipDslId(), persistedDataIds.kitJsonDslId());
    }

    private CreateAssessmentKitDslPort.Param toCreateAssessmentKitDslParam(UploadKitPort.Result result) {
        return new CreateAssessmentKitDslPort.Param(
            result.zipFilePath(),
            result.jsonFilePath()
        );
    }
}
