package org.flickit.assessment.common.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("app.file")
@RequiredArgsConstructor
public class FileProperties {

    @NotNull
    DataSize pictureMaxSize = DataSize.ofMegabytes(2);

    @NotNull
    DataSize dslMaxSize = DataSize.ofMegabytes(5);
}
