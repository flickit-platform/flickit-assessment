package org.flickit.assessment.core.application.port.out.kitcustom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public interface LoadKitCustomPort {

    KitCustomData loadCustomDataByIdAndKitId(long kitCustomId, long kitId);

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    record KitCustomData(@JsonProperty("subs") List<Subject> subjects,
                                @JsonProperty("atts") List<Attribute> attributes) {

        public record Subject(@JsonProperty("id") long id, @JsonProperty("w") int weight) {
        }

        public record Attribute(@JsonProperty("id") long id, @JsonProperty("w") int weight) {
        }
    }
}
