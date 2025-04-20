package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.util.SpringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentUseCaseParamTest {

    @Mock
    ApplicationContext applicationContext;

    @BeforeEach
    void prepare() {
        var props = new AppSpecProperties();
        lenient().doReturn(props).when(applicationContext).getBean(AppSpecProperties.class);
        new SpringUtil(applicationContext);
    }

    @Test
    void testCreateAssessmentUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_ASSESSMENT_TITLE_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("a")));
        assertThat(throwable).hasMessage("title: " + CREATE_ASSESSMENT_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.random(101, true, true))));
        assertThat(throwable).hasMessage("title: " + CREATE_ASSESSMENT_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateAssessmentUseCaseParam_shortTitleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.shortTitle("a")));
        assertThat(throwable).hasMessage("shortTitle: " + CREATE_ASSESSMENT_SHORT_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.shortTitle(RandomStringUtils.random(21, true, true))));
        assertThat(throwable).hasMessage("shortTitle: " + CREATE_ASSESSMENT_SHORT_TITLE_SIZE_MAX);

        assertDoesNotThrow(() -> createParam(b -> b.shortTitle(null)));
        assertDoesNotThrow(() -> createParam(b -> b.shortTitle("          ")));
    }

    @Test
    void testCreateAssessmentUseCaseParam_spaceIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.spaceId(null)));
        assertThat(throwable).hasMessage("spaceId: " + CREATE_ASSESSMENT_SPACE_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentUseCaseParam_kitIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + CREATE_ASSESSMENT_ASSESSMENT_KIT_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentUseCaseParam_whenLangParamIsInvalid_thenSetLangToDefault() {
        var param = createParam(b -> b.lang("FR"));
        assertEquals("EN", param.getLang());
    }

    @Test
    void testCreateAssessmentUseCaseParam_whenLangParamIsNullOrEmpty_thenSetLangToNull() {
        var param = createParam(b -> b.lang(" "));
        assertNull(param.getLang());

        param = createParam(b -> b.lang(null));
        assertNull(param.getLang());
    }

    @Test
    void testCreateAssessmentUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private CreateAssessmentUseCase.Param createParam(Consumer<CreateAssessmentUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAssessmentUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAssessmentUseCase.Param.builder()
            .title("title")
            .shortTitle("shortTitle")
            .spaceId(123L)
            .kitId(234L)
            .lang("EN")
            .currentUserId(UUID.randomUUID());
    }
}
