package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_EMAIL_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_SPACE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AddSpaceMemberUseCaseParamTest {

    @Test
    void testAddSpaceMember_spaceIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        String email= "admin@asta.com";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddSpaceMemberUseCase.Param(null, email, currentUserId));
        assertThat(throwable).hasMessage("spaceId: " + ADD_SPACE_MEMBER_SPACE_ID_NOT_NULL);
    }

    @Test
    void testAddSpaceMember_emailIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        long spaceId = 0L;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddSpaceMemberUseCase.Param(spaceId, null, currentUserId));
        assertThat(throwable).hasMessage("email: " + ADD_SPACE_MEMBER_EMAIL_NOT_NULL);
    }

    @Test
    void testAddSpaceMember_currentUserIdIsNull_ErrorMessage() {
        long spaceId = 0L;
        String email= "admin@asta.com";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddSpaceMemberUseCase.Param(spaceId, email, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
