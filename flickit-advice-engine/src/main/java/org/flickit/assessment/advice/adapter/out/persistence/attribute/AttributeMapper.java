package org.flickit.assessment.advice.adapter.out.persistence.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.advice.AttributeListItem;
import org.flickit.assessment.data.jpa.kit.question.advice.AttributeAdviceView;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeMapper {

    public static AttributeListItem mapToListItem(AttributeAdviceView view) {
        return new AttributeListItem(view.getId(), view.getTitle());
    }
}
