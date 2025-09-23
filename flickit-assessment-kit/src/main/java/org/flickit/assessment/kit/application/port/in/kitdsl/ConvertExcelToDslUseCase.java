package org.flickit.assessment.kit.application.port.in.kitdsl;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CONVERT_EXCEL_TO_DSL_EXCEL_FILE_NOT_NULL;

public interface ConvertExcelToDslUseCase {

    void convertExcelToDsl(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CONVERT_EXCEL_TO_DSL_EXCEL_FILE_NOT_NULL)
        MultipartFile excelFile;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(MultipartFile excelFile, UUID currentUserId) {
            this.excelFile = excelFile;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
