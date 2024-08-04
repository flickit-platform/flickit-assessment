package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.EMAIL_FORMAT_NOT_VALID;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_EMAIL_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_SPACE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AddSpaceMemberUseCaseParamTest {

    @Test
    void testAddSpaceMember_spaceIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        String email = "admin@asta.com";
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
    void testAddSpaceMember_EmailIsNotValid_ErrorMessage() {
        long spaceId = 0L;
        String email = "test.com";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddSpaceMemberUseCase.Param(spaceId, email, currentUserId));
        assertThat(throwable).hasMessage("email: " + EMAIL_FORMAT_NOT_VALID);
    }

    @Test
    void testAddSpaceMember_EmailIsBlank_ErrorMessage() {
        long spaceId = 0L;
        String email = " ";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddSpaceMemberUseCase.Param(spaceId, email, currentUserId));
        assertThat(throwable).hasMessage("email: " + ADD_SPACE_MEMBER_EMAIL_NOT_NULL);
    }

    @Test
    void testAddSpaceMember_Email_SuccessfulStripAndIgnoreCase() {
        long spaceId = 0L;
        String email1 = "test@test.com";
        String email2 = " Test@test.com    ";
        UUID currentUserId = UUID.randomUUID();
        var param1 = new AddSpaceMemberUseCase.Param(spaceId, email1, currentUserId);
        var param2 = new AddSpaceMemberUseCase.Param(spaceId, email2, currentUserId);
        assertEquals(param1.getEmail(), param2.getEmail(), "The input email should be stripped, and the case should be ignored.");
    }

    @Test
    void testAddSpaceMember_currentUserIdIsNull_ErrorMessage() {
        long spaceId = 0L;
        String email = "admin@asta.com";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddSpaceMemberUseCase.Param(spaceId, email, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
