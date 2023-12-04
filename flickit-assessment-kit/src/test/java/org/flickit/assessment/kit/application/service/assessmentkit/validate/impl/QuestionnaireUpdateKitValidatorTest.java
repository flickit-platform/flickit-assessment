package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.common.Notification;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionnaireDslModelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.COLLECTION;
import static org.flickit.assessment.kit.application.service.assessmentkit.validate.impl.DslFieldNames.QUESTIONNAIRE;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithQuestionnaires;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class QuestionnaireUpdateKitValidatorTest {

    public static final String QUESTIONNAIRE_TITLE_1 = "Questionnaire1";
    public static final String QUESTIONNAIRE_TITLE_2 = "Questionnaire2";
    public static final String NEW = "New";
    @InjectMocks
    private QuestionnaireUpdateKitValidator validator;


    @Test
    void testValidate_SameQuestionnairesInDbAndDsl_Valid() {
        var questionnaireOne = questionnaireWithTitle(QUESTIONNAIRE_TITLE_1);
        var questionnaireTwo = questionnaireWithTitle(QUESTIONNAIRE_TITLE_2);
        var savedKit = kitWithQuestionnaires(List.of(questionnaireOne, questionnaireTwo));

        var dslQuestionnaireOne = QuestionnaireDslModelMother.domainToDslModel(questionnaireOne, q -> q.title(NEW + QUESTIONNAIRE_TITLE_1));
        var dslQuestionnaireTwo = QuestionnaireDslModelMother.domainToDslModel(questionnaireTwo, q -> q.title(NEW + QUESTIONNAIRE_TITLE_2));
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaireOne, dslQuestionnaireTwo))
            .build();

        Notification notification = validator.validate(savedKit, dslKit);

        assertFalse(notification.hasErrors());
    }

    @Test
    void testValidate_NewQuestionnaireInDsl_Valid() {
        var questionnaireOne = questionnaireWithTitle(QUESTIONNAIRE_TITLE_1);
        var savedKit = kitWithQuestionnaires(List.of(questionnaireOne));

        var dslQuestionnaireOne = QuestionnaireDslModelMother.domainToDslModel(questionnaireOne);
        var dslQuestionnaireNew = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(NEW + QUESTIONNAIRE_TITLE_1));
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaireOne, dslQuestionnaireNew))
            .build();

        Notification notification = validator.validate(savedKit, dslKit);

        assertFalse(notification.hasErrors());
    }

    @Test
    void testValidate_dslHasOneQuestionnaireLessThanDsl_Invalid() {
        var questionnaireOne = questionnaireWithTitle(QUESTIONNAIRE_TITLE_1);
        var questionnaireTwo = questionnaireWithTitle(QUESTIONNAIRE_TITLE_2);
        var savedKit = kitWithQuestionnaires(List.of(questionnaireOne, questionnaireTwo));

        var dslQuestionnaireOne = QuestionnaireDslModelMother.domainToDslModel(questionnaireOne, q -> q.title(NEW + QUESTIONNAIRE_TITLE_1));
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaireOne))
            .build();

        Notification notification = validator.validate(savedKit, dslKit);

        assertThat(notification)
            .returns(true, Notification::hasErrors)
            .extracting(Notification::getErrors, as(COLLECTION))
            .singleElement()
            .isInstanceOfSatisfying(InvalidDeletionError.class, x -> {
                assertThat(x.fieldName()).isEqualTo(QUESTIONNAIRE);
                assertThat(x.deletedItems()).contains(questionnaireTwo.getCode());
            });
    }
}
