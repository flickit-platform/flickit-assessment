package org.flickit.assessment.kit.application.domain.dsl;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AnswerOptionDslModel {

    Integer index;
    String caption;
    Integer value; //TODO it's unused and can be removed
}
