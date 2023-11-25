package org.flickit.assessment.kit.adapter.out.updatekitbydsl;

import lombok.AllArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.LoadLevelCompetencesByMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesByKitPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.FIND_KIT_ID_NOT_FOUND;

@Component
@AllArgsConstructor
public class LoadAssessmentKitInfoAdapter implements LoadAssessmentKitInfoPort {

    private final AssessmentKitJpaRepository repository;
    private final LoadMaturityLevelByKitPort loadMaturityLevelByKitPort;
    private final LoadLevelCompetencesByMaturityLevelPort loadLevelCompetencesByMaturityLevelPort;
    private final LoadQuestionnairesByKitPort loadQuestionnairesByKitPort;

    @Override
    public AssessmentKit load(Long kitId) {
        AssessmentKitJpaEntity entity = repository.findById(kitId).orElseThrow(
            () -> new ResourceNotFoundException(FIND_KIT_ID_NOT_FOUND));
        List<MaturityLevel> levels = new ArrayList<>(loadMaturityLevelByKitPort.loadByKitId(kitId));
        setLevelCompetences(levels);

        ArrayList<Questionnaire> questionnaires = new ArrayList<>(loadQuestionnairesByKitPort.loadByKitId(kitId));

        return new AssessmentKit(
            kitId,
            entity.getCode(),
            entity.getTitle(),
            entity.getSummary(),
            entity.getAbout(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getIsActive(),
            entity.getExpertGroupId(),
            null,
            levels,
            questionnaires
        );
    }

    private void setLevelCompetences(List<MaturityLevel> levels) {
        levels.forEach(level -> level.setCompetences(
            loadLevelCompetencesByMaturityLevelPort.loadByMaturityLevelId(level.getId())));
    }

}
