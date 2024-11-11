package org.flickit.assessment.kit.application.service.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.in.kitversion.ActivateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitActiveVersionPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitLastMajorModificationTimePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.kitversion.UpdateKitVersionStatusPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionsPort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.LoadSubjectQuestionnairePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.ACTIVATE_KIT_VERSION_STATUS_INVALID;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivateKitVersionService implements ActivateKitVersionUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateKitVersionStatusPort updateKitVersionStatusPort;
    private final UpdateKitActiveVersionPort updateKitActiveVersionPort;
    private final LoadSubjectQuestionnairePort loadSubjectQuestionnairePort;
    private final CreateSubjectQuestionnairePort createSubjectQuestionnairePort;
    private final UpdateKitLastMajorModificationTimePort updateKitLastMajorModificationTimePort;
    private final LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;
    private final LoadQuestionsPort loadQuestionsPort;
    private final CreateAnswerOptionImpactPort createAnswerOptionImpactPort;

    @Override
    public void activateKitVersion(Param param) {
        Long kitVersionId = param.getKitVersionId();
        var kitVersion = loadKitVersionPort.load(kitVersionId);
        if (!KitVersionStatus.UPDATING.equals(kitVersion.getStatus()))
            throw new ValidationException(ACTIVATE_KIT_VERSION_STATUS_INVALID);

        var kit = kitVersion.getKit();
        var ownerId = loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (kit.getActiveVersionId() != null)
            updateKitVersionStatusPort.updateStatus(kit.getActiveVersionId(), KitVersionStatus.ARCHIVE);

        updateKitVersionStatusPort.updateStatus(kitVersionId, KitVersionStatus.ACTIVE);
        updateKitActiveVersionPort.updateActiveVersion(kit.getId(), kitVersionId);
        updateKitLastMajorModificationTimePort.updateLastMajorModificationTime(kit.getId(), LocalDateTime.now());

        createSubjectQuestionnaires(kitVersionId);

        createAnswerOptionImpacts(kitVersionId, param.getCurrentUserId());
    }

    private void createSubjectQuestionnaires(Long kitVersionId) {
        var subjectQuestionnaires = loadSubjectQuestionnairePort.extractPairs(kitVersionId).stream()
            .collect(groupingBy(
                SubjectQuestionnaire::getQuestionnaireId,
                mapping(SubjectQuestionnaire::getSubjectId, toSet())));

        createSubjectQuestionnairePort.persistAll(subjectQuestionnaires, kitVersionId);
    }

    private void createAnswerOptionImpacts(Long kitVersionId, UUID currentUserId) {
        List<Question> questions = loadQuestionsPort.loadAllByKitVersionId(kitVersionId);
        Set<Long> rangeIds = questions.stream()
            .map(Question::getAnswerRangeId)
            .collect(toSet());

        var answerOptions = loadAnswerOptionsByQuestionPort.loadByRangeIdInAndKitVersionId(rangeIds, kitVersionId);
        Map<Long, List<AnswerOption>> rangeIdToOptions = answerOptions.stream()
            .collect(groupingBy(AnswerOption::getAnswerRangeId));

        List<QuestionImpact> qImpacts = questions.stream()
            .map(Question::getImpacts)
            .flatMap(Collection::stream)
            .toList();

        Map<Long, Long> questionIdToRangeId = questions.stream()
            .collect(toMap(Question::getId, Question::getAnswerRangeId));

        List<CreateAnswerOptionImpactPort.Param> outPortParams = qImpacts.stream()
            .map(qImpact -> {
                Long rangeId = questionIdToRangeId.get(qImpact.getQuestionId());
                List<AnswerOption> options = rangeIdToOptions.get(rangeId);
                return options.stream()
                    .map(option -> new CreateAnswerOptionImpactPort.Param(qImpact.getId(),
                        option.getId(),
                        null,
                        kitVersionId,
                        currentUserId))
                    .toList();
            })
            .flatMap(Collection::stream)
            .toList();

        createAnswerOptionImpactPort.persistAll(outPortParams);
    }
}
