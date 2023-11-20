package org.flickit.assessment.kit.adapter.out.updatekitbydsl;

import lombok.AllArgsConstructor;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.LoadLevelCompetencesByMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_KIT_NOT_FOUND;

@Component
@AllArgsConstructor
public class LoadAssessmentKitInfoAdapter implements LoadAssessmentKitInfoPort {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final LoadMaturityLevelByKitPort loadMaturityLevelByKitPort;
    private final LoadLevelCompetencesByMaturityLevelPort loadLevelCompetencesByMaturityLevelPort;

    @Override
    public AssessmentKit load(Long kitId) {
        AssessmentKit assessmentKit = loadAssessmentKitPort.load(kitId).orElseThrow(
            () -> new ResourceNotFoundException(UPDATE_KIT_BY_DSL_KIT_NOT_FOUND));
        List<MaturityLevel> levels = new ArrayList<>(loadMaturityLevelByKitPort.loadByKitId(kitId));
        setLevelIndexes(levels);
        setLevelCompetences(levels);

        return new AssessmentKit(
            kitId,
            assessmentKit.getCode(),
            assessmentKit.getTitle(),
            assessmentKit.getSummary(),
            assessmentKit.getAbout(),
            assessmentKit.getCreationTime(),
            assessmentKit.getLastModificationTime(),
            assessmentKit.isPublished(),
            assessmentKit.getExpertGroupId(),
            null,
            levels,
            null
        );
    }

    private void setLevelIndexes(List<MaturityLevel> levels) {
        levels.sort(Comparator.comparing(MaturityLevel::getId));
        for (int i = 0; i < levels.size(); i++) {
            levels.get(i).setIndex(i);
        }
    }

    private void setLevelCompetences(List<MaturityLevel> levels) {
        levels.forEach(level -> level.setCompetences(
            loadLevelCompetencesByMaturityLevelPort.loadByMaturityLevelId(level.getId())));
    }
}
