package org.flickit.assessment.kit.application.port.out.questionnaire;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionnaireTranslation;
import org.flickit.assessment.kit.application.domain.Question;

import java.util.List;
import java.util.Map;

public interface LoadKitQuestionnaireDetailPort {

    Result loadKitQuestionnaireDetail(Long questionnaireId, Long kitVersionId);

    record Result(int questionsCount, String description, List<Question> questions, Map<KitLanguage, QuestionnaireTranslation> translations) {}
}
