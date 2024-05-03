package org.flickit.assessment.core.application.service.subject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.SubjectReport;
import org.flickit.assessment.core.application.port.in.subject.ReportSubjectUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.MaturityLevel.middleLevel;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportSubjectService implements ReportSubjectUseCase {

    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadSubjectReportInfoPort loadSubjectReportInfoPort;
    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Override
    public Result reportSubject(Param param) {
        if (!checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var subjectReport = loadSubjectReportInfoPort.load(param.getAssessmentId(), param.getSubjectId());

        List<MaturityLevel> maturityLevels = subjectReport.maturityLevels().stream()
            .map(e -> new MaturityLevel(e.id(), e.index(), e.value(), null))
            .toList();
        var midLevelMaturity = middleLevel(maturityLevels);
        List<SubjectReport.AttributeReportItem> attributes = subjectReport.attributes();
        List<Result.TopAttribute> topStrengths = attributes.stream()
            .sorted(Comparator.comparing(e -> e.maturityLevel().index(), Comparator.reverseOrder()))
            .filter(e -> midLevelMaturity.getIndex() <= e.maturityLevel().index())
            .limit(3)
            .map(e -> new Result.TopAttribute(e.id(), e.title()))
            .toList();

        List<Result.TopAttribute> topWeaknesses = attributes.stream()
            .sorted(Comparator.comparing(e -> e.maturityLevel().index()))
            .filter(e -> e.maturityLevel().index() <= midLevelMaturity.getIndex())
            .limit(3)
            .map(e -> new Result.TopAttribute(e.id(), e.title()))
            .toList();

        return new Result(
            subjectReport.subject(),
            topStrengths,
            topWeaknesses,
            subjectReport.attributes(),
            subjectReport.maturityLevels().size());
    }
}
