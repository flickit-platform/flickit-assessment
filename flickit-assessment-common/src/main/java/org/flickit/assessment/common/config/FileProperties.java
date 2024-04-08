package org.flickit.assessment.common.config;

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

    DataSize pictureMaxSize = DataSize.ofMegabytes(1);

    DataSize dslMaxSize = DataSize.ofMegabytes(5);
}
