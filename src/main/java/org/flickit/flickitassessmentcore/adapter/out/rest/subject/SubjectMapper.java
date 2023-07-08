package org.flickit.flickitassessmentcore.adapter.out.rest.subject;

import org.flickit.flickitassessmentcore.adapter.out.rest.subject.SubjectRestAdapter.SubjectDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.subject.SubjectRestAdapter.QualityAttributeDto;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectIdsAndQualityAttributeIdsPort.ResponseParam;

import java.util.List;

public class SubjectMapper {

    public static ResponseParam toResponseParam(List<SubjectDto> responseBody) {
        List<Long> subjectIds = responseBody.stream().map(SubjectDto::id).toList();
        List<Long> qualityAttributeIds = responseBody.stream()
            .map(x -> x.qualityAttributes().stream().map(QualityAttributeDto::id).toList())
            .flatMap(List::stream).toList();
        return new ResponseParam(subjectIds, qualityAttributeIds);
    }
}
