package org.flickit.assessment.kit.adapter.in.rest.subject;

import org.flickit.assessment.common.application.domain.kit.translation.SubjectTranslation;

import java.util.Map;

public record CreateSubjectRequestDto(Integer index,
                                      String title,
                                      String description,
                                      Integer weight,
                                      Map<String, SubjectTranslation> translations) {
}
