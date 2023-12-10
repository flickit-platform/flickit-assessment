package org.flickit.assessment.kit.application.domain.dsl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AttributeDslModel extends BaseDslModel {

    String subjectCode;
    Integer weight;
}
