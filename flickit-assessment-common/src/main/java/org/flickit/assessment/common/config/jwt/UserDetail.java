package org.flickit.assessment.common.config.jwt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDetail(@JsonProperty("sub") UUID id) {
}
