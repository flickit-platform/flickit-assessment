package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.KitTranslation;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.common.util.SpringUtil;
import org.flickit.assessment.kit.application.domain.KitMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UpdateKitInfoUseCaseParamTest {

    @Mock
    ApplicationContext applicationContext;

    @BeforeEach
    void prepare() {
        var props = new AppSpecProperties();
        lenient().doReturn(props).when(applicationContext).getBean(AppSpecProperties.class);
        new SpringUtil(applicationContext);
    }

    @Test
    void testUpdateKitInfoUseCaseParam_kitIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + UPDATE_KIT_INFO_KIT_ID_NOT_NULL);
    }

    @Test
    void testUpdateKitInfoUseCaseParam_TitleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + UPDATE_KIT_INFO_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(51))));
        assertThat(throwable).hasMessage("title: " + UPDATE_KIT_INFO_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfoUseCaseParam_summaryParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.summary("ab")));
        assertThat(throwable).hasMessage("summary: " + UPDATE_KIT_INFO_SUMMARY_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.summary(RandomStringUtils.randomAlphabetic(201))));
        assertThat(throwable).hasMessage("summary: " + UPDATE_KIT_INFO_SUMMARY_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfoUseCaseParam_aboutParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.about("ab")));
        assertThat(throwable).hasMessage("about: " + UPDATE_KIT_INFO_ABOUT_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.about(RandomStringUtils.randomAlphabetic(1001))));
        assertThat(throwable).hasMessage("about: " + UPDATE_KIT_INFO_ABOUT_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfoUseCaseParam_langParamViolatesConstraints_SetDefault() {
        var param = createParam(b -> b.lang("FR"));
        assertEquals(KitLanguage.getDefault().name(), param.getLang());

        param = createParam(b -> b.lang(null));
        assertNull(param.getLang());
    }

    @Test
    void testUpdateKitInfoUseCaseParam_tagsParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.tags(new ArrayList<>())));
        assertThat(throwable).hasMessage("tags: " + UPDATE_KIT_INFO_TAGS_SIZE_MIN);
    }

    @Test
    void testUpdateKitInfoUseCaseParam_translationsLanguageViolations_ErrorMessage() {
        var throwable = assertThrows(ValidationException.class,
            () -> createParam(a -> {
                a.translations(Map.of("FR", new KitTranslation("title", "summary", "about", null)));
                a.removeTranslations(false);
            }));
        assertEquals(COMMON_KIT_LANGUAGE_NOT_VALID, throwable.getMessageKey());
    }

    @Test
    void testUpdateKitInfoUseCaseParam_translationsFieldIsNotCorrect_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> {
                a.translations(Map.of("EN", new KitTranslation("title", "summary", "about", null)));
                a.removeTranslations(true);
            }));
        assertThat(throwable).hasMessage("translationFieldCorrect: " + UPDATE_KIT_INFO_TRANSLATIONS_INCORRECT);
    }

    @Test
    void testUpdateKitInfoUseCaseParam_metadataFieldIsNotCorrect_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> {
                a.metadata(new KitMetadata("goal", "context"));
                a.removeMetadata(true);
            }));
        assertThat(throwable).hasMessage("metadataFieldCorrect: " + UPDATE_KIT_INFO_METADATA_INCORRECT);
    }

    @Test
    void testUpdateKitInfoUseCaseParam_metadataFieldIsCorrect_Success() {
        assertDoesNotThrow(
            () -> createParam(a -> {
                a.metadata(null);
                a.removeMetadata(true);
            }));
    }

    @Test
    void testUpdateKitInfoUseCaseParam_translationsFieldsViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new KitTranslation("t", "summary", "about", null)))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_ASSESSMENT_KIT_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new KitTranslation(RandomStringUtils.randomAlphabetic(51), "desc", "about", null)))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_ASSESSMENT_KIT_TITLE_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new KitTranslation("title", "su", "about", null)))));
        assertThat(throwable).hasMessage("translations[EN].summary: " + TRANSLATION_ASSESSMENT_KIT_SUMMARY_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new KitTranslation("title", RandomStringUtils.randomAlphabetic(201), "about", null)))));
        assertThat(throwable).hasMessage("translations[EN].summary: " + TRANSLATION_ASSESSMENT_KIT_SUMMARY_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new KitTranslation("title", "summary", "a", null)))));
        assertThat(throwable).hasMessage("translations[EN].about: " + TRANSLATION_ASSESSMENT_KIT_ABOUT_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new KitTranslation("title", "summary", RandomStringUtils.randomAlphabetic(1001), null)))));
        assertThat(throwable).hasMessage("translations[EN].about: " + TRANSLATION_ASSESSMENT_KIT_ABOUT_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new KitTranslation("title", "summary", "about",
                new KitTranslation.MetadataTranslation("g", "context"))))));
        assertThat(throwable).hasMessage("translations[EN].metadata.goal: " + TRANSLATION_ASSESSMENT_KIT_METADATA_GOAL_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new KitTranslation("title", "summary", "about",
                new KitTranslation.MetadataTranslation(RandomStringUtils.randomAlphabetic(1001), "context"))))));
        assertThat(throwable).hasMessage("translations[EN].metadata.goal: " + TRANSLATION_ASSESSMENT_KIT_METADATA_GOAL_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new KitTranslation("title", "summary", "about",
                new KitTranslation.MetadataTranslation("goal", "c"))))));
        assertThat(throwable).hasMessage("translations[EN].metadata.context: " + TRANSLATION_ASSESSMENT_KIT_METADATA_CONTEXT_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new KitTranslation("title", "summary", "about",
                new KitTranslation.MetadataTranslation("goal", RandomStringUtils.randomAlphabetic(1001)))))));
        assertThat(throwable).hasMessage("translations[EN].metadata.context: " + TRANSLATION_ASSESSMENT_KIT_METADATA_CONTEXT_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfoUseCaseParam_metadataFieldsViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.metadata(new KitMetadata("go", "context"))));
        assertThat(throwable).hasMessage("metadata.goal: " + KIT_METADATA_GOAL_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.metadata(new KitMetadata(RandomStringUtils.randomAlphabetic(1001), "context"))));
        assertThat(throwable).hasMessage("metadata.goal: " + KIT_METADATA_GOAL_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.metadata(new KitMetadata("goal", "co"))));
        assertThat(throwable).hasMessage("metadata.context: " + KIT_METADATA_CONTEXT_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.metadata(new KitMetadata("goal", RandomStringUtils.randomAlphabetic(1001)))));
        assertThat(throwable).hasMessage("metadata.context: " + KIT_METADATA_CONTEXT_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfoUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private UpdateKitInfoUseCase.Param createParam(Consumer<UpdateKitInfoUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private UpdateKitInfoUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateKitInfoUseCase.Param.builder()
            .kitId(1L)
            .title("title")
            .summary("summary")
            .about("about")
            .lang("EN")
            .published(true)
            .isPrivate(false)
            .price(10d)
            .tags(List.of(1L))
            .currentUserId(UUID.randomUUID());
    }
}
