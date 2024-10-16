package org.flickit.assessment.kit.application.domain.dsl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Value
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MaturityLevelDslModel extends BaseDslModel {

    @JsonProperty("levelCompetence")
    Map<String, Integer> competencesCodeToValueMap;
    Integer value;
}
