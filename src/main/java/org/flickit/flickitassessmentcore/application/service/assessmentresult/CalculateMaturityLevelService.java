package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultByAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubjectPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadQualityAttributeByResultPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.UpdateQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.LoadSubjectValueByResultPort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.UpdateSubjectValuePort;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class CalculateMaturityLevelService implements CalculateMaturityLevelUseCase {

    private final LoadAssessmentPort loadAssessmentPort;
    private final LoadAssessmentResultByAssessmentPort loadAssessmentResultByAssessmentPort;
    private final LoadMaturityLevelByKitPort loadMaturityLevelByKitPort;
    private final UpdateAssessmentResultPort updateAssessmentResultPort;
    private final UpdateQualityAttributeValuePort updateQualityAttributeValuePort;
    private final UpdateSubjectValuePort updateSubjectValuePort;
    private final LoadQualityAttributeBySubjectPort loadQualityAttributeBySubjectPort;
    private final LoadQualityAttributeByResultPort loadQualityAttributeByResultPort;
    private final LoadSubjectValueByResultPort loadSubjectValueByResultPort;

    private final CalculateQualityAttributeMaturityLevel calculateQualityAttributeMaturityLevel;
    private final CalculateSubjectMaturityLevel calculateSubjectMaturityLevel;
    private final CalculateAssessmentMaturityLevel calculateAssessmentMaturityLevel;

    /**
     * In this method, maturity level of quality attribute , subject and assessment are been calculated
     * and saved in assessment result. <br>
     * - quality attribute maturity level is calculated by calculating weighted mean of question answers and their impacts. <br>
     * - subject maturity level is calculated by calculating weighted mean of quality attribute maturity levels. <br>
     * - At the end, the maturity level of assessment is calculated by calculating weighted mean of subject maturity levels. <br>
     * All of them are stored in result and saved in database. <br>
     */
    @Override
    public Result calculateMaturityLevel(Param param) {
        Assessment assessment = loadAssessmentPort.load(param.getAssessmentId());
        List<AssessmentResult> assessmentResults = loadAssessmentResultByAssessmentPort.loadByAssessmentId(assessment.getId()).results();
        AssessmentResult assessmentResult = assessmentResults.get(0); // For now, we have just 1 assessmentResult.

        if (assessmentResult.getIsValid()) {
            return new Result(assessmentResult.getId());
        }

        Long assessmentKitId = assessmentResult.getAssessment().getAssessmentKitId();
        List<MaturityLevel> maturityLevels = loadMaturityLevelByKitPort.loadByKitId(assessmentKitId).maturityLevels();

        List<QualityAttributeValue> qualityAttributeValues = calculateQualityAttributesMaturityLevel(assessmentResult, maturityLevels);
        List<SubjectValue> subjectValues = calculateSubjectsMaturityLevel(assessmentResult, maturityLevels, qualityAttributeValues);
        MaturityLevel assessmentMaturityLevel = calculateAssessmentMaturityLevel(maturityLevels, subjectValues);

        assessmentResult.setMaturityLevelId(assessmentMaturityLevel.getId());
        assessmentResult.setIsValid(Boolean.TRUE);

        UUID updatedResultId = updateAssessmentResultPort.update(assessmentResult);
        return new Result(updatedResultId);
    }

    private List<QualityAttributeValue> calculateQualityAttributesMaturityLevel(AssessmentResult assessmentResult, List<MaturityLevel> maturityLevels) {
        LoadQualityAttributeByResultPort.Param param = new LoadQualityAttributeByResultPort.Param(assessmentResult.getId());
        List<QualityAttributeValue> qualityAttributeValues = loadQualityAttributeByResultPort.loadByResultId(param).qualityAttributeValues();
        for (QualityAttributeValue qualityAttributeValue : qualityAttributeValues) {
            MaturityLevel qualityAttributeValueMaturityLevel = calculateQualityAttributeMaturityLevel.calculate(
                assessmentResult.getId(),
                maturityLevels,
                qualityAttributeValue.getQualityAttribute().getId()
            );
            qualityAttributeValue.setMaturityLevel(qualityAttributeValueMaturityLevel);
            qualityAttributeValue.setResultId(assessmentResult.getId());
            updateQualityAttributeValuePort.update(qualityAttributeValue);
        }
        return qualityAttributeValues;
    }

    private List<SubjectValue> calculateSubjectsMaturityLevel(AssessmentResult assessmentResult, List<MaturityLevel> maturityLevels, List<QualityAttributeValue> qualityAttributeValues) {
        List<SubjectValue> subjectValues = loadSubjectValueByResultPort
            .loadByResultId(new LoadSubjectValueByResultPort.Param(assessmentResult.getId())).subjectValues();
        for (SubjectValue subjectValue : subjectValues) {
            List<QualityAttribute> qualityAttributes = loadQualityAttributeBySubjectPort.loadBySubjectId(subjectValue.getSubject().getId()).qualityAttribute();
            List<QualityAttributeValue> qualityAttributeValueList = new ArrayList<>();
            for (QualityAttributeValue qualityAttributeValue : qualityAttributeValues) {
                List<QualityAttribute> matchedQualityAttributes = qualityAttributes.stream()
                    .filter(qai -> Objects.equals(qai.getId(), qualityAttributeValue.getQualityAttribute().getId()))
                    .toList();
                if (!matchedQualityAttributes.isEmpty()) {
                    qualityAttributeValue.getQualityAttribute().setWeight(matchedQualityAttributes.get(0).getWeight());
                    qualityAttributeValueList.add(qualityAttributeValue);
                }
            }
            MaturityLevel subjectMaturityLevel = calculateSubjectMaturityLevel.calculate(maturityLevels, qualityAttributeValueList);
            subjectValue.setMaturityLevel(subjectMaturityLevel);
            subjectValue.setResultId(assessmentResult.getId());
            updateSubjectValuePort.update(subjectValue);
        }
        return subjectValues;
    }

    private MaturityLevel calculateAssessmentMaturityLevel(List<MaturityLevel> maturityLevels, List<SubjectValue> subjectValues) {
        return calculateAssessmentMaturityLevel.calculate(maturityLevels, subjectValues);
    }

}
