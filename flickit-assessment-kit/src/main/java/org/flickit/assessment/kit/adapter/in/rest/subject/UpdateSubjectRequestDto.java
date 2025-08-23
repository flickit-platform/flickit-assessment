package org.flickit.assessment.kit.adapter.in.rest.subject;

import org.flickit.assessment.common.application.domain.kit.translation.SubjectTranslation;

import java.util.Map;

public record UpdateSubjectRequestDto(int index,
                                      String title,
                                      String description,
                                      int weight,
                                      Map<String, SubjectTranslation> translations) {
}
