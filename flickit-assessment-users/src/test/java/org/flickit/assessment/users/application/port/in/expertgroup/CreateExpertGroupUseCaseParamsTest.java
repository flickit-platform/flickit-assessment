package org.flickit.assessment.users.application.port.in.expertgroup;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateExpertGroupUseCaseParamsTest {

    @Test
    void testCreateExpertGroupUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_EXPERT_GROUP_TITLE_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.random(2, true, true))));
        assertThat(throwable).hasMessage("title: " + CREATE_EXPERT_GROUP_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.random(101, true, true))));
        assertThat(throwable).hasMessage("title: " + CREATE_EXPERT_GROUP_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateExpertGroupUseCaseParam_bioParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.bio(null)));
        assertThat(throwable).hasMessage("bio: " + CREATE_EXPERT_GROUP_BIO_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.bio(RandomStringUtils.random(2, true, true))));
        assertThat(throwable).hasMessage("bio: " + CREATE_EXPERT_GROUP_BIO_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.bio(RandomStringUtils.random(201, true, true))));
        assertThat(throwable).hasMessage("bio: " + CREATE_EXPERT_GROUP_BIO_SIZE_MAX);
    }

    @Test
    void testCreateExpertGroupUseCaseParam_aboutParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.about(null)));
        assertThat(throwable).hasMessage("about: " + CREATE_EXPERT_GROUP_ABOUT_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.about(RandomStringUtils.random(2, true, true))));
        assertThat(throwable).hasMessage("about: " + CREATE_EXPERT_GROUP_ABOUT_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.about(RandomStringUtils.random(501, true, true))));
        assertThat(throwable).hasMessage("about: " + CREATE_EXPERT_GROUP_ABOUT_SIZE_MAX);
    }

    @Test
    void testCreateExpertGroupUseCaseParam_websiteParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.website("invalid-url")));
        assertThat(throwable).hasMessage("website: " + CREATE_EXPERT_GROUP_WEBSITE_NOT_URL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.website("https://" + RandomStringUtils.randomAlphabetic(201) + ".com")));
        assertThat(throwable).hasMessage("website: " + CREATE_EXPERT_GROUP_WEBSITE_SIZE_MAX);
    }

    @Test
    void testCreateExpertGroupUseCaseParam_websiteIsNull_Successful() {
        assertDoesNotThrow(() -> createParam(b -> b.website(null)));
    }

    @Test
    void testCreateExpertGroupUseCaseParam_currentUserIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testCreateExpertGroupUseCaseParam_validParams_DoesNotThrowError() {
        assertDoesNotThrow(
            () -> createParam(b -> b.title("Valid Title")
                .bio("Valid Bio")
                .about("Valid About")
                .website("https://valid-website.com")
                .currentUserId(UUID.randomUUID()))
        );
    }

    private void createParam(Consumer<CreateExpertGroupUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateExpertGroupUseCase.Param.ParamBuilder paramBuilder() {
        return CreateExpertGroupUseCase.Param.builder()
            .title("title")
            .bio("bio")
            .about("about")
            .website("https://valid-website.com")
            .currentUserId(UUID.randomUUID());
    }
}
