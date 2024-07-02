package org.flickit.assessment.users.application.port.in.expertgroup;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static io.jsonwebtoken.lang.Classes.getResourceAsStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateExpertGroupPictureUseCaseParamTest {

    @Test
    void testUpdateExpertGroupPictureParam_idIsNull_ErrorMessage() throws IOException {
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupPictureUseCase.Param(null, picture , currentUserId));
        assertThat(throwable).hasMessage("expertGroupId: " + UPDATE_EXPERT_GROUP_PICTURE_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testUpdateExpertGroupPictureParam_pictureIsNull_ErrorMessage() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupPictureUseCase.Param(expertGroupId, null , currentUserId));
        assertThat(throwable).hasMessage("picture: " + UPDATE_EXPERT_GROUP_PICTURE_PICTURE_NOT_NULL);
    }

    @Test
    void testUpdateExpertGroupPictureParam_pictureIsEmpty_ErrorMessage() throws IOException {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", getResourceAsStream("/no-where/nothing.png"));
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupPictureUseCase.Param(expertGroupId, picture , currentUserId));
        assertThat(throwable).hasMessage("picture: " + UPDATE_EXPERT_GROUP_PICTURE_PICTURE_NOT_NULL);
    }

    @Test
    void testUpdateExpertGroupPictureParam_currentUserIdIsNull_ErrorMessage() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupPictureUseCase.Param(expertGroupId, picture , null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
