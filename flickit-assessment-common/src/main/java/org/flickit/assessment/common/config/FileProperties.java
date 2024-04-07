package org.flickit.assessment.common.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@RequiredArgsConstructor
public class FileProperties {

    @NotBlank
    DataSize pictureMaxSize = DataSize.ofMegabytes(2);

    @NotBlank
    DataSize kitMaxSize = DataSize.ofMegabytes(5);
}
