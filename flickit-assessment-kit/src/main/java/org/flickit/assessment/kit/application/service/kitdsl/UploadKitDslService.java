package org.flickit.assessment.kit.application.service.kitdsl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.kitdsl.UploadKitDslUseCase;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateKitDslPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.ParsDslFilePort;
import org.flickit.assessment.kit.application.port.out.kitdsl.UploadKitDslToFileStoragePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;

@Service
@Transactional
@RequiredArgsConstructor
public class UploadKitDslService implements UploadKitDslUseCase {

    private final FileProperties fileProperties;
    private final ParsDslFilePort parsDslFilePort;
    private final UploadKitDslToFileStoragePort uploadKitDslToFileStoragePort;
    private final CreateKitDslPort createKitDslPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @SneakyThrows
    @Override
    public Long upload(UploadKitDslUseCase.Param param) {
        validateFile(param.getDslFile());

        UUID currentUserId = param.getCurrentUserId();
        validateCurrentUser(param.getExpertGroupId(), currentUserId);

        AssessmentKitDslModel dslContentJson = parsDslFilePort.parsToDslModel(param.getDslFile());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(dslContentJson);
        UploadKitDslToFileStoragePort.Result filesInfo = uploadKitDslToFileStoragePort.uploadKitDsl(param.getDslFile(), json);
        return createKitDslPort.create(filesInfo.dslFilePath(), filesInfo.jsonFilePath(), currentUserId);
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private void validateFile(MultipartFile dslFile) {
        if (dslFile.getSize() >= fileProperties.getDslMaxSize().toBytes())
            throw new ValidationException(UPLOAD_FILE_DSL_SIZE_MAX);

        if (!fileProperties.getKitDslContentTypes().contains(dslFile.getContentType()))
            throw new ValidationException(UPLOAD_FILE_FORMAT_NOT_VALID);
    }
}
