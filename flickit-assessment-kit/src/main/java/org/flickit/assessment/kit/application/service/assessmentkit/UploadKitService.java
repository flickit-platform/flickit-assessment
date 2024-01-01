package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UploadKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.GetDslContentPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UploadKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UploadKitService implements UploadKitUseCase {

    private final UploadKitPort uploadKitPort;
    private final GetDslContentPort getDslContentPort;

    @Override
    public String upload(Param param) {
        uploadKitPort.upload(param.getDslFile());
        return getDslContentPort.getDslContent(param.getDslFile());
    }
}
