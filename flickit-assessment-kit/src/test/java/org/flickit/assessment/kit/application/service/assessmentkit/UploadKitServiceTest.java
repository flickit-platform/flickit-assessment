package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.GetDslContentPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UploadKitPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.zip.ZipFile;

@ExtendWith(MockitoExtension.class)
class UploadKitServiceTest {

    @InjectMocks
    private UploadKitService service;

    @Mock
    private UploadKitPort uploadKitPort;

    @Mock
    private GetDslContentPort getDslContentPort;

    @Mock
    private CreateAssessmentKitDslPort createAssessmentKitDslPort;

    @SneakyThrows
    @Test
    void testUploadKit_ValidKitFile_ValidResult() {
        ZipFile zipFile = new ZipFile("../../test/java/org/flickit/assessment/kit/correct-kit.zip");
    }

    @Test
    void testUploadKit_InvalidKitFile_DslSyntaxError() {

    }


}
