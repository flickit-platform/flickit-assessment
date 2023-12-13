package org.flickit.assessment.common.config.jwt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDetail(@JsonProperty("preferred_username") String username,
                         @JsonProperty("email") String email,
                         @JsonProperty("email_verified") Boolean emailVerified,
                         @JsonProperty("given_name") String firstName,
                         @JsonProperty("family_name") String lastName,
                         @JsonProperty("name") String fullName) {
}
