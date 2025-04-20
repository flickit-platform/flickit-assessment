package org.flickit.assessment.kit.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerRangeTranslation;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class AnswerRange {

    private final Long id;
    private final String code;
    private final String title;
    private final boolean reusable;
    private final List<AnswerOption> answerOptions;
    @Setter
    private Map<KitLanguage, AnswerRangeTranslation> translations;
}
