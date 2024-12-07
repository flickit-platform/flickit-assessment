package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AnswerRangeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.answerrange.UpdateAnswerRangePort;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_ANSWER_RANGES;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerRangeUpdateKitPersister implements UpdateKitPersister {

    private final CreateAnswerRangePort createAnswerRangePort;
    private final UpdateAnswerRangePort updateAnswerRangePort;
    private final UpdateAnswerOptionPort updateAnswerOptionPort;

    @Override
    public int order() {
        return 5;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx,
                                            AssessmentKit savedKit,
                                            AssessmentKitDslModel dslKit,
                                            UUID currentUserId) {
        Map<String, AnswerRange> savedRangesCodesMap = savedKit.getReusableAnswerRanges().stream().collect(toMap(AnswerRange::getCode, i -> i));
        Map<String, Long> addedCodeToIdMap = new HashMap<>();

        boolean isMajorUpdate = false;
        for (AnswerRangeDslModel dslRange : dslKit.getAnswerRanges()) {
            var savedRange = savedRangesCodesMap.get(dslRange.getCode());

            if (savedRange == null) {
                Long persistedSubjectId = createAnswerRangePort.persist(toCreateParam(dslRange, savedKit.getActiveVersionId(), currentUserId));
                addedCodeToIdMap.put(dslRange.getCode(), persistedSubjectId);
                log.debug("AnswerRange[id={}, code={}] created", persistedSubjectId, dslRange.getCode());
            } else {
                if (!savedRange.getTitle().equals(dslRange.getTitle())) {
                    updateAnswerRangePort.update(toUpdateParam(savedRange.getId(), savedKit.getActiveVersionId(), dslRange, currentUserId));
                    log.debug("AnswerRange[id={}, code={}] updated", savedRange.getId(), savedRange.getCode());
                }
                var isOptionValueChanged = updateAnswerOptions(savedRange, dslRange, savedKit.getActiveVersionId(), currentUserId);
                if (isOptionValueChanged)
                    isMajorUpdate = true;
            }
        }


        Map<String, Long> updatedCodeToIdMap = savedRangesCodesMap.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getId()));
        HashMap<String, Long> rangeCodeToIdMap = new HashMap<>(addedCodeToIdMap);
        rangeCodeToIdMap.putAll(updatedCodeToIdMap);

        ctx.put(KEY_ANSWER_RANGES, rangeCodeToIdMap);
        log.debug("Final answer ranges: {}", rangeCodeToIdMap);

        return new UpdateKitPersisterResult(isMajorUpdate);
    }

    private CreateAnswerRangePort.Param toCreateParam(AnswerRangeDslModel dslAnswerRange, long kitVersionId, UUID currentUserId) {
        return new CreateAnswerRangePort.Param(
            kitVersionId,
            dslAnswerRange.getTitle(),
            dslAnswerRange.getCode(),
            true,
            currentUserId
        );
    }

    private UpdateAnswerRangePort.Param toUpdateParam(long id, long kitVersionId, AnswerRangeDslModel dslSubject, UUID currentUserId) {
        return new UpdateAnswerRangePort.Param(id,
            kitVersionId,
            dslSubject.getTitle(),
            dslSubject.getCode(),
            true,
            LocalDateTime.now(),
            currentUserId
        );
    }

    private boolean updateAnswerOptions(AnswerRange savedRange, AnswerRangeDslModel dslRange, Long kitVersionId, UUID currentUserId) {
        Map<Integer, AnswerOption> savedOptionIndexMap = savedRange.getAnswerOptions().stream()
            .collect(toMap(AnswerOption::getIndex, a -> a));

        Map<Integer, AnswerOptionDslModel> dslOptionIndexMap = dslRange.getAnswerOptions().stream()
            .collect(toMap(AnswerOptionDslModel::getIndex, a -> a));

        var isOptionValueChanged = false;

        for (Map.Entry<Integer, AnswerOption> optionEntry : savedOptionIndexMap.entrySet()) {
            var savedOption = optionEntry.getValue();
            var dslOption = dslOptionIndexMap.get(optionEntry.getKey());

            if (savedOption.getValue() != dslOption.getValue()) {
                updateAnswerOptionPort.update(new UpdateAnswerOptionPort.Param(
                    savedOption.getId(),
                    kitVersionId,
                    savedOption.getIndex(),
                    dslOption.getCaption(),
                    dslOption.getValue(),
                    LocalDateTime.now(),
                    currentUserId));
                isOptionValueChanged = true;
                log.debug("AnswerOption[id={}, index={}, newTitle{}, value{}, answerRangeId{}] updated.",
                    savedOption.getId(), savedOption.getIndex(), dslOption.getCaption(), savedOption.getValue(), savedRange.getId());
            } else if (!savedOption.getTitle().equals(dslOption.getCaption())) {
                updateAnswerOptionPort.updateTitle(new UpdateAnswerOptionPort.UpdateTitleParam(
                    savedOption.getId(),
                    kitVersionId,
                    dslOption.getCaption(),
                    LocalDateTime.now(),
                    currentUserId));
                log.debug("AnswerOption[id={}, index={}, title{}, newValue{}, answerRangeId{}] updated.",
                    savedOption.getId(), savedOption.getIndex(), dslOption.getCaption(), dslOption.getValue(), savedRange.getId());
            }
        }
        return isOptionValueChanged;
    }
}
