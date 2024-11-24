package org.flickit.assessment.kit.application.service.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitCustom;
import org.flickit.assessment.kit.application.port.in.kitcustom.GetKitCustomUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.LoadKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitCustomService implements GetKitCustomUseCase {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final CheckKitUserAccessPort checkKitUserAccessPort;
    private final LoadKitCustomPort loadKitCustomPort;

    @Override
    public Result getKitCustom(Param param) {
        var kitCustom = loadKitCustomPort.load(param.getKitCustomId());
        AssessmentKit kit = loadAssessmentKitPort.load(kitCustom.getKitId());

        if (kit.isPrivate() && !checkKitUserAccessPort.hasAccess(kitCustom.getKitId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return toResult(kitCustom);
    }

    private Result toResult(KitCustom kitCustom) {
        var subjects = kitCustom.getCustomData().subjects() != null ?
            kitCustom.getCustomData().subjects().stream()
                .map(s -> new Result.ResultCustomData.Data(s.id(), s.weight()))
                .toList() :
            null;
        var attributes = kitCustom.getCustomData().attributes() != null ?
            kitCustom.getCustomData().attributes().stream()
                .map(a -> new Result.ResultCustomData.Data(a.id(), a.weight()))
                .toList() :
            null;
        return new GetKitCustomUseCase.Result(kitCustom.getTitle(), new Result.ResultCustomData(subjects, attributes));
    }
}
