package org.flickit.assessment.scenario.fixture.request;

import lombok.experimental.UtilityClass;
import org.springframework.mock.web.MockMultipartFile;

@UtilityClass
public class MultiPartFileMother {

    public static MockMultipartFile picture() {
        return new MockMultipartFile(
            "picture",
            "test-image.jpg",
            "image/jpeg",
            "Sample image content".getBytes()
        );
    }
}
