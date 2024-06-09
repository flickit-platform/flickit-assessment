package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitStatsUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitStatsService implements GetKitStatsUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final CountKitStatsPort countKitStatsPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;

    @Override
    public Result getKitStats(Param param) {
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var counts = countKitStatsPort.countKitStats(param.getKitId());

        AssessmentKit assessmentKit = loadAssessmentKitPort.load(param.getKitId());

        List<KitStatSubject> subjects = loadSubjectsPort.loadByKitVersionId(assessmentKit.getKitVersionId()).stream()
            .map(s -> new GetKitStatsUseCase.KitStatSubject(s.getTitle()))
            .toList();

        return new GetKitStatsUseCase.Result(
            assessmentKit.getCreationTime(),
            assessmentKit.getLastModificationTime(),
            counts.questionnairesCount(),
            counts.attributesCount(),
            counts.questionsCount(),
            counts.maturityLevelsCount(),
            counts.likes(),
            counts.assessmentCounts(),
            subjects,
            new GetKitStatsUseCase.KitStatExpertGroup(expertGroup.getId(), expertGroup.getTitle())
        );
    }
}
