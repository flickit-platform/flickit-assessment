package org.flickit.assessment.advice.adapter.out.persistence.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.advice.AdviceAttribute;
import org.flickit.assessment.data.jpa.kit.question.advice.AttributeAdviceView;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeMapper {

    public static AdviceAttribute mapToListItem(AttributeAdviceView view) {
        return new AdviceAttribute(view.getId(), view.getTitle());
    }
}
