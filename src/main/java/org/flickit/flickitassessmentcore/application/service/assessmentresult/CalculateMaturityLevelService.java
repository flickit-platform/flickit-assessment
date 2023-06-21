package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.*;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.SaveAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.SaveAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubjectvalue.SaveAssessmentSubjectValuePort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.SaveQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.domain.*;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.*;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultByAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubject.LoadAssessmentSubjectByAssessmentKitPort;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateMaturityLevelService implements CalculateMaturityLevelUseCase {

    private final LoadAssessmentPort loadAssessment;
    private final LoadAssessmentResultByAssessmentPort loadAssessmentResultByAssessment;
    private final LoadAssessmentSubjectByAssessmentKitPort loadSubjectByKit;
    private final LoadQualityAttributeBySubPort loadQualityAttributeBySubject;
    private final SaveAssessmentResultPort saveAssessmentResult;
    private final SaveAssessmentPort saveAssessment;
    private final SaveQualityAttributeValuePort saveQualityAttributeValue;
    private final SaveAssessmentSubjectValuePort saveAssessmentSubjectValue;

    private final CalculateQualityAttributeMaturityLevel calculateQualityAttributeMaturityLevel;
    private final CalculateAssessmentSubjectMaturityLevel calculateAssessmentSubjectMaturityLevel;
    private final CalculateAssessmentMaturityLevel calculateAssessmentMaturityLevel;

    /**
     * In this method, maturity level (ml) of quality attribute (qa), subject (sub) and assessment are been calculated
     * and saved in assessment result (result). <br>
     * - qa ml is calculated by calculating weighted mean of question answers and their impacts. <br>
     * - sub ml is calculated by calculating weighted mean of qa mls. <br>
     * - At the end, the ml of assessment is calculated by calculating weighted mean of sub mls. <br>
     * All of them are stored in result and saved in db. <br>
     */
    @Override
    public AssessmentResult calculateMaturityLevel(CalculateMaturityLevelCommand command) {
        Assessment assessment = loadAssessment.loadAssessment(command.getAssessmentId());
        List<AssessmentResult> results = new ArrayList<>(loadAssessmentResultByAssessment.loadAssessmentResultByAssessmentId(assessment.getId()));
        AssessmentResult result = results.get(0);
        if (!result.isValid()) {
            List<AssessmentSubject> subjects = loadSubjectByKit.loadSubjectByKitId(assessment.getAssessmentKitId());
            for (AssessmentSubject subject : subjects) {
                List<QualityAttribute> qualityAttributes = loadQualityAttributeBySubject.loadQABySubId(subject.getId());
                List<QualityAttributeValue> qualityAttributeValues = new ArrayList<>();
                for (QualityAttribute qualityAttribute : qualityAttributes) {
                    QualityAttributeValue qualityAttributeValue = calculateQualityAttributeMaturityLevel.calculateQualityAttributeMaturityLevel(
                        result,
                        qualityAttribute
                    );
                    qualityAttributeValues.add(qualityAttributeValue);
                    saveQualityAttributeValue.saveQualityAttributeValue(qualityAttributeValue);
                }
                result.getQualityAttributeValues().addAll(qualityAttributeValues);
            }

            for (AssessmentSubject subject : subjects) {
                AssessmentSubjectValue assessmentSubjectValue = calculateAssessmentSubjectMaturityLevel.calculateAssessmentSubjectMaturityLevel(subject);
                result.getAssessmentSubjectValues().add(assessmentSubjectValue);
                saveAssessmentSubjectValue.saveAssessmentSubjectValue(assessmentSubjectValue);
            }

            MaturityLevel assessmentMaturityLevel = calculateAssessmentMaturityLevel.calculateAssessmentMaturityLevel(result.getAssessmentSubjectValues());
            assessment.setMaturityLevel(assessmentMaturityLevel);
            result.getAssessment().setMaturityLevel(assessmentMaturityLevel);

            saveAssessment.saveAssessment(assessment);
            return saveAssessmentResult.saveAssessmentResult(result);
        }
        return result;
    }

}
