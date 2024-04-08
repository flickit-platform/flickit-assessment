package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagsListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitEditableInfoService implements GetKitEditableInfoUseCase {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final LoadKitTagsListPort loadKitTagsListPort;

    @Override
    public KitEditableInfo getKitEditableInfo(Param param) {
        AssessmentKit assessmentKit = loadAssessmentKitPort.load(param.getKitId());

        List<KitTag> tags = loadKitTagsListPort.load(param.getKitId());

        return new KitEditableInfo(
            assessmentKit.getId(),
            assessmentKit.getTitle(),
            assessmentKit.getSummary(),
            assessmentKit.isPublished(),
            0D,
            assessmentKit.getAbout(),
            tags);
    }
}
