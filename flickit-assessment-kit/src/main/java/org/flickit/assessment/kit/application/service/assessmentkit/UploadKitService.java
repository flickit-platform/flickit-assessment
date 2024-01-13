package org.flickit.assessment.kit.application.service.assessmentkit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UploadKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.GetDslContentPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UploadKitDslToFileStoragePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class UploadKitService implements UploadKitUseCase {

    private final UploadKitDslToFileStoragePort uploadKitDslToFileStoragePort;
    private final GetDslContentPort getDslContentPort;
    private final CreateKitDslPort createKitDslPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @SneakyThrows
    @Override
    public Long upload(UploadKitUseCase.Param param) {
        validateCurrentUser(param.getExpertGroupId(), param.getCurrentUserId());

        AssessmentKitDslModel dslContentJson = getDslContentPort.getDslContent(param.getDslFile());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(dslContentJson);
        UploadKitDslToFileStoragePort.Result uploadedFilesInformation = uploadKitDslToFileStoragePort.upload(param.getDslFile(), json);
        return createKitDslPort.create(toCreateAssessmentKitDslParam(uploadedFilesInformation));
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));
        if (!Objects.equals(expertGroupOwnerId, currentUserId)) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }

    private CreateKitDslPort.Param toCreateAssessmentKitDslParam(UploadKitDslToFileStoragePort.Result result) {
        return new CreateKitDslPort.Param(
            result.zipFilePath(),
            result.jsonFilePath()
        );
    }
}
