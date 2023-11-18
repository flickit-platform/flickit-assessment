package org.flickit.assessment.kit.application.domain.dsl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class MaturityLevelDslModel extends BaseDslModel {

    @JsonProperty("levelCompetence")
    private Map<String, Integer> competencesCodeToValueMap;
    private Integer value;
}
