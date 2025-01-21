package org.flickit.assessment.core.adapter.in.rest.assessmentreport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public record CreateAssessmentReportMetadataRequestDto(@JsonIgnoreProperties(ignoreUnknown = true) String intro,
                                                       @JsonIgnoreProperties(ignoreUnknown = true) String prosAnsCons,
                                                       @JsonIgnoreProperties(ignoreUnknown = true) String steps,
                                                       @JsonIgnoreProperties(ignoreUnknown = true) String participants) {
}
