package org.flickit.assessment.advice.adapter.out.persistence.option;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.advice.OptionListItem;
import org.flickit.assessment.data.jpa.kit.question.advice.OptionAdviceView;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionMapper {

    public static OptionListItem mapToListItem(OptionAdviceView view) {
        return new OptionListItem(view.getIndex(), view.getTitle());
    }
}
