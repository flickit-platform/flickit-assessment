package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import java.util.function.Consumer;

import static io.jsonwebtoken.lang.Classes.getResourceAsStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_USER_PROFILE_PICTURE_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateUserProfilePictureUseCaseParamTest {

    @Test
    @SneakyThrows
    void testUpdateUserProfilePictureParam_pictureParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.picture(null)));
        assertThat(throwable).hasMessage("picture: " + UPDATE_USER_PROFILE_PICTURE_NOT_NULL);

        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", getResourceAsStream("/no-where/nothing.png"));
        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.picture(picture)));
        assertThat(throwable).hasMessage("picture: " + UPDATE_USER_PROFILE_PICTURE_NOT_NULL);
    }

    @Test
    void testUpdateUserProfilePictureParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @SneakyThrows
    private void createParam(Consumer<UpdateUserProfilePictureUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    @SneakyThrows
    private UpdateUserProfilePictureUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateUserProfilePictureUseCase.Param.builder()
            .currentUserId(UUID.randomUUID())
            .picture(new MockMultipartFile("images", "image1",
                "image/png", new ByteArrayInputStream("Some content".getBytes())));
    }
}
