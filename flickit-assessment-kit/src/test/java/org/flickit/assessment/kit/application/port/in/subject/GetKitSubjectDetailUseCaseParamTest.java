package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_SUBJECT_DETAIL_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetKitSubjectDetailUseCaseParamTest {

    public static final Long KIT_ID = 25L;
    public static final Long SUBJECT_ID = 11L;
    public static final UUID CURRENT_USER_ID = UUID.randomUUID();

    @Test
    void testGetKitSubjectDetail_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitSubjectDetailUseCase.Param(null, SUBJECT_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("kitId: " + GET_KIT_SUBJECT_DETAIL_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetKitSubjectDetail_subjectIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitSubjectDetailUseCase.Param(KIT_ID, null, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("subjectId: " + GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testGetKitSubjectDetail_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitSubjectDetailUseCase.Param(KIT_ID, SUBJECT_ID, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
