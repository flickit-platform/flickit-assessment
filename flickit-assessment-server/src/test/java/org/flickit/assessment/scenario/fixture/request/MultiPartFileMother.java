package org.flickit.assessment.scenario.fixture.request;

import org.springframework.mock.web.MockMultipartFile;

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
