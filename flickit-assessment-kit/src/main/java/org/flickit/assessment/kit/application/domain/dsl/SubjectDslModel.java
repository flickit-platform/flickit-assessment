package org.flickit.assessment.kit.application.domain.dsl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SubjectDslModel extends BaseDslModel {

    Integer weight;
    List<String> questionnaireCodes; //TODO Always null
}
