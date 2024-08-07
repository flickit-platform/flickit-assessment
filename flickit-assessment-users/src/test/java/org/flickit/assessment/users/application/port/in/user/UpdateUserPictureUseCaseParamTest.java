package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static io.jsonwebtoken.lang.Classes.getResourceAsStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_USER_PICTURE_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateUserPictureUseCaseParamTest {

    @Test
    void testUpdateUserPictureParam_pictureIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserPictureUseCase.Param(currentUserId, null));
        assertThat(throwable).hasMessage("picture: " + UPDATE_USER_PICTURE_NOT_NULL);
    }

    @Test
    void testUpdateUserPictureParam_pictureIsEmpty_ErrorMessage() throws IOException {
        UUID currentUserId = UUID.randomUUID();
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", getResourceAsStream("/no-where/nothing.png"));
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserPictureUseCase.Param(currentUserId, picture));
        assertThat(throwable).hasMessage("picture: " + UPDATE_USER_PICTURE_NOT_NULL);
    }

    @Test
    void testUpdateUserPictureParam_currentUserIdIsNull_ErrorMessage() throws IOException {
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserPictureUseCase.Param(null, picture));
        assertThat(throwable).hasMessage("userId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
