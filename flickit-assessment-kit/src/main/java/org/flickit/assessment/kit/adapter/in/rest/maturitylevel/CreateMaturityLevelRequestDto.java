package org.flickit.assessment.kit.adapter.in.rest.maturitylevel;

import org.flickit.assessment.common.application.domain.kit.translation.MaturityLevelTranslation;

import java.util.Map;

public record CreateMaturityLevelRequestDto(Integer index,
                                            String title,
                                            String description,
                                            Integer value,
                                            Map<String, MaturityLevelTranslation> translations) {
}
