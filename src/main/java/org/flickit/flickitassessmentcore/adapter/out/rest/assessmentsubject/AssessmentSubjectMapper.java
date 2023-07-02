package org.flickit.flickitassessmentcore.adapter.out.rest.assessmentsubject;

import org.flickit.flickitassessmentcore.adapter.out.rest.assessmentsubject.AssessmentSubjectRestAdapter.AssessmentSubjectDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.assessmentsubject.AssessmentSubjectRestAdapter.QualityAttributeDto;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubject.LoadAssessmentSubjectIdsAndQualityAttributeIdsPort.ResponseParam;

import java.util.List;

public class AssessmentSubjectMapper {

    public static ResponseParam toResponseParam(List<AssessmentSubjectDto> responseBody) {
        List<Long> assessmentSubjectIds = responseBody.stream().map(AssessmentSubjectDto::id).toList();
        List<Long> qualityAttributeIds = responseBody.stream()
            .map(x -> x.qualityAttributes().stream().map(QualityAttributeDto::id).toList())
            .flatMap(List::stream).toList();
        return new ResponseParam(assessmentSubjectIds, qualityAttributeIds);
    }
}
