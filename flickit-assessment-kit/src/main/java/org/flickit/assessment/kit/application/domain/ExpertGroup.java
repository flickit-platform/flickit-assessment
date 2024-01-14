package org.flickit.assessment.kit.application.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExpertGroup {

    private final long id;
    private final String title;
    private final String bio;
    private final String about;
    private final String picture;
    private final String website;
    private final boolean isOwner;
    @JsonProperty("isOwner")
    public boolean isOwner() {
        return isOwner;
    }
}
