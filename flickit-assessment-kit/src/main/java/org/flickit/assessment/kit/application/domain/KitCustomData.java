package org.flickit.assessment.kit.application.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record KitCustomData(@JsonProperty("subs") List<Subject> subjects,
                            @JsonProperty("atts") List<Attribute> attributes) {

    public record Subject(@JsonProperty("id") long id, @JsonProperty("w") int weight) {
    }

    public record Attribute(@JsonProperty("id") long id, @JsonProperty("w") int weight) {
    }
}
