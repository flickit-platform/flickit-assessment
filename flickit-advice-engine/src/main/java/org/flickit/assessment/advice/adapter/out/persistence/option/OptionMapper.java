package org.flickit.assessment.advice.adapter.out.persistence.option;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.advice.AdviceOption;
import org.flickit.assessment.data.jpa.kit.question.advice.OptionAdviceView;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionMapper {

    public static AdviceOption mapToListItem(OptionAdviceView view) {
        return new AdviceOption(view.getIndex(), view.getTitle());
    }
}
