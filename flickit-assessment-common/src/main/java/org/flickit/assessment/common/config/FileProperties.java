package org.flickit.assessment.common.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;

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

    @NotNull
    private List<String> pictureContentTypes = Arrays.asList(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/bmp"
    );
}
