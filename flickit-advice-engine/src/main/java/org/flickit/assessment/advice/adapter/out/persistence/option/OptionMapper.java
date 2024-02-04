package org.flickit.assessment.advice.adapter.out.persistence.option;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.advice.AdviceOptionListItem;
import org.flickit.assessment.data.jpa.kit.question.advice.OptionAdviceView;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionMapper {

    public static AdviceOptionListItem mapToListItem(OptionAdviceView view) {
        return new AdviceOptionListItem(view.getIndex(), view.getTitle());
    }
}
