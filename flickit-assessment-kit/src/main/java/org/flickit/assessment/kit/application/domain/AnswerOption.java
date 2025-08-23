package org.flickit.assessment.kit.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerOptionTranslation;

import java.util.Map;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class AnswerOption {

    private final long id;
    private final String title;
    private final int index;
    private final Long answerRangeId;
    private final double value;

    @Setter
    private Map<KitLanguage, AnswerOptionTranslation> translations;
}
