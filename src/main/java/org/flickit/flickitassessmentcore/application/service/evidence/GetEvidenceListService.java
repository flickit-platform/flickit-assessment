package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencesByQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GetEvidenceListService implements GetEvidenceListUseCase {

    private final LoadEvidencesByQuestionPort loadEvidencesByQuestion;

    @Override
    public Result getEvidenceList(Param param) {
        return new Result(loadEvidencesByQuestion.loadEvidencesByQuestionId(new LoadEvidencesByQuestionPort.Param(param.getQuestionId())).evidences());
    }
}
