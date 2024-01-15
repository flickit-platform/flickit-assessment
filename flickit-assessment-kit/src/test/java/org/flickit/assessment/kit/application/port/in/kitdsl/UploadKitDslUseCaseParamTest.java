package org.flickit.assessment.kit.application.port.in.kitdsl;

import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.application.service.kitdsl.UploadKitServiceTest.ZIP_FILE_ADDR;
import static org.flickit.assessment.kit.application.service.kitdsl.UploadKitServiceTest.convertZipFileToMultipartFile;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPLOAD_KIT_DSL_EXPERT_GROUP_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPLOAD_KIT_DSL_KIT_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UploadKitDslUseCaseParamTest {

    @Test
    void testUploadKit_DslFileIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        Long expertGroupId = 1L;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UploadKitDslUseCase.Param(null, expertGroupId, currentUserId));
        assertThat(throwable).hasMessage("dslFile: " + UPLOAD_KIT_DSL_KIT_NOT_NULL);
    }

    @SneakyThrows
    @Test
    void testUploadKit_ExpertGroupIdIsNull_ErrorMessage() {
        MultipartFile dslFile = convertZipFileToMultipartFile(ZIP_FILE_ADDR);
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UploadKitDslUseCase.Param(dslFile, null, currentUserId));
        assertThat(throwable).hasMessage("expertGroupId: " + UPLOAD_KIT_DSL_EXPERT_GROUP_ID_NOT_NULL);
    }

    @SneakyThrows
    @Test
    void testUploadKit_CurrentUserIdIsNull_ErrorMessage() {
        MultipartFile dslFile = convertZipFileToMultipartFile(ZIP_FILE_ADDR);
        Long expertGroupId = 1L;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UploadKitDslUseCase.Param(dslFile, expertGroupId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
