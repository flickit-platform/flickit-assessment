package org.flickit.assessment.kit.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionTranslation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Question {

    private final long id;
    private final String code;
    private final String title;
    private final int index;
    private final String hint;
    private final Boolean mayNotBeApplicable;
    private final Boolean advisable;
    @Setter
    private List<QuestionImpact> impacts;
    private final Long answerRangeId;
    private final Long measureId;
    @Setter
    private List<AnswerOption> options;
    private final Long questionnaireId;
    @Setter
    private Map<KitLanguage, QuestionTranslation> translations;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;

    public static String generateCode(int index) {
        return "m" + index;
    }
}
