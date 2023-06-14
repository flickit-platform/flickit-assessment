package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.domain.QualityAttribute;

import java.util.List;

public interface LoadQABySubIdPort {

    List<QualityAttribute> loadQABySubId(Long subId);
}
