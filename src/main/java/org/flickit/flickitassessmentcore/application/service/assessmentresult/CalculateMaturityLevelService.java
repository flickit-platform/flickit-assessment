package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultByAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.SaveAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.SaveSubjectValuePort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubjectPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadQualityAttributeByResultPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.SaveQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.LoadSubjectValueByResultPort;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateMaturityLevelService implements CalculateMaturityLevelUseCase {

    private final LoadAssessmentPort loadAssessmentPort;
    private final LoadAssessmentResultByAssessmentPort loadAssessmentResultByAssessmentPort;
    private final SaveAssessmentResultPort saveAssessmentResultPort;
    private final SaveQualityAttributeValuePort saveQualityAttributeValuePort;
    private final SaveSubjectValuePort saveSubjectValuePort;
    private final LoadQualityAttributeBySubjectPort loadQualityAttributeBySubjectPort;
    private final LoadQualityAttributeByResultPort loadQualityAttributeByResultPort;
    private final LoadSubjectValueByResultPort loadSubjectValueByResultPort;

    private final CalculateQualityAttributeMaturityLevel calculateQualityAttributeMaturityLevel;
    private final CalculateAssessmentSubjectMaturityLevel calculateAssessmentSubjectMaturityLevel;
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
    public CalculateMaturityLevelUseCase.Result calculateMaturityLevel(CalculateMaturityLevelUseCase.Param param) {
        Assessment assessment = loadAssessmentPort.loadAssessment(param.getAssessmentId());
        List<AssessmentResult> results = new ArrayList<>(loadAssessmentResultByAssessmentPort.loadAssessmentResultByAssessmentId(assessment.getId()));
        AssessmentResult result = results.get(0); // For now, we have just 1 result.
        if (!result.getIsValid()) {
            List<QualityAttributeValue> qualityAttributeValues = loadQualityAttributeByResultPort
                .loadQualityAttributeByResultId(new LoadQualityAttributeByResultPort.Param(result.getId())).qualityAttributeValues();
            for (QualityAttributeValue qualityAttributeValue : qualityAttributeValues) {
                MaturityLevel qualityAttributeValueMaturityLevel = calculateQualityAttributeMaturityLevel.calculateQualityAttributeMaturityLevel(
                    result,
                    qualityAttributeValue.getQualityAttribute(),
                    assessment.getAssessmentKitId()
                );
                qualityAttributeValue.setMaturityLevel(qualityAttributeValueMaturityLevel);
                qualityAttributeValue.setResultId(result.getId());
                saveQualityAttributeValuePort.saveQualityAttributeValue(qualityAttributeValue);
            }

            List<SubjectValue> subjectValues = loadSubjectValueByResultPort
                .loadSubjectValueByResultId(new LoadSubjectValueByResultPort.Param(result.getId())).subjectValues();
            for (SubjectValue subjectValue : subjectValues) {
                List<QualityAttribute> qualityAttributes = loadQualityAttributeBySubjectPort.loadQualityAttributeBySubjectId(new LoadQualityAttributeBySubjectPort.Param(subjectValue.getAssessmentSubject().getId())).qualityAttribute();
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
                MaturityLevel subjectMaturityLevel = calculateAssessmentSubjectMaturityLevel.calculateAssessmentSubjectMaturityLevel(qualityAttributeValueList, assessment.getAssessmentKitId());
                subjectValue.setMaturityLevel(subjectMaturityLevel);
                subjectValue.setResultId(result.getId());
                saveSubjectValuePort.saveSubjectValue(subjectValue);
            }

            MaturityLevel assessmentMaturityLevel = calculateAssessmentMaturityLevel.calculateAssessmentMaturityLevel(subjectValues, assessment.getAssessmentKitId());
            result.setMaturityLevelId(assessmentMaturityLevel.getId());

            AssessmentResult savedResult = saveAssessmentResultPort.saveAssessmentResult(result);
            return new CalculateMaturityLevelUseCase.Result(savedResult.getId());
        }
        return new CalculateMaturityLevelUseCase.Result(result.getId());
    }

}
