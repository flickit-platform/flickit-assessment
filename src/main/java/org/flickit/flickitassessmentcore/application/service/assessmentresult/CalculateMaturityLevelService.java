package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultByAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.SaveAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubjectvalue.SaveAssessmentSubjectValuePort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.SaveQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateMaturityLevelService implements CalculateMaturityLevelUseCase {

    private final LoadAssessmentPort loadAssessment;
    private final LoadAssessmentResultByAssessmentPort loadAssessmentResultByAssessment;
    private final SaveAssessmentResultPort saveAssessmentResult;
    private final SaveQualityAttributeValuePort saveQualityAttributeValue;
    private final SaveAssessmentSubjectValuePort saveAssessmentSubjectValue;

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
        Assessment assessment = loadAssessment.loadAssessment(param.getAssessmentId());
        List<AssessmentResult> results = new ArrayList<>(loadAssessmentResultByAssessment.loadAssessmentResultByAssessmentId(assessment.getId()));
        AssessmentResult result = results.get(0); // For now, we have just 1 result.
        if (!result.getIsValid()) {
            List<QualityAttributeValue> qualityAttributeValues = result.getQualityAttributeValues();
            for (QualityAttributeValue qualityAttributeValue : qualityAttributeValues) {
                MaturityLevel qualityAttributeValueMaturityLevel = calculateQualityAttributeMaturityLevel.calculateQualityAttributeMaturityLevel(
                    result,
                    qualityAttributeValue.getQualityAttribute(),
                    assessment.getAssessmentKitId()
                );
                qualityAttributeValue.setMaturityLevel(qualityAttributeValueMaturityLevel);
                saveQualityAttributeValue.saveQualityAttributeValue(qualityAttributeValue);
            }
            result.setQualityAttributeValues(qualityAttributeValues);

            List<AssessmentSubjectValue> assessmentSubjectValues = result.getAssessmentSubjectValues();
            for (AssessmentSubjectValue subjectValue : assessmentSubjectValues) {
                List<QualityAttributeValue> qualityAttributeValueList = qualityAttributeValues.stream()
                    .filter(qav -> Objects.equals(qav.getQualityAttribute().getAssessmentSubject().getId(), subjectValue.getAssessmentSubject().getId()))
                    .collect(Collectors.toList());
                MaturityLevel subjectMaturityLevel = calculateAssessmentSubjectMaturityLevel.calculateAssessmentSubjectMaturityLevel(qualityAttributeValueList, assessment.getAssessmentKitId());
                subjectValue.setMaturityLevel(subjectMaturityLevel);
                saveAssessmentSubjectValue.saveAssessmentSubjectValue(subjectValue);
            }
            result.setAssessmentSubjectValues(assessmentSubjectValues);

            MaturityLevel assessmentMaturityLevel = calculateAssessmentMaturityLevel.calculateAssessmentMaturityLevel(result.getAssessmentSubjectValues(), assessment.getAssessmentKitId());
            result.setMaturityLevelId(assessmentMaturityLevel.getId());

            AssessmentResult savedResult = saveAssessmentResult.saveAssessmentResult(result);
            return new CalculateMaturityLevelUseCase.Result(savedResult.getId());
        }
        return new CalculateMaturityLevelUseCase.Result(result.getId());
    }

}
