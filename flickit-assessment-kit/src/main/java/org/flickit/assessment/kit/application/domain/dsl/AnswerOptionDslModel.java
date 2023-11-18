package org.flickit.assessment.kit.application.domain.dsl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerOptionDslModel {

    private Integer index;
    private String caption;
    private Integer value; //TODO it's unused and can be removed
}
