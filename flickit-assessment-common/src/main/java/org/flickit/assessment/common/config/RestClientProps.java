package org.flickit.assessment.common.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class RestClientProps {

    @NotNull
    private Duration connectTimeout = Duration.ofSeconds(5);

    @NotNull
    private Duration readTimeout = Duration.ofSeconds(5);
}
