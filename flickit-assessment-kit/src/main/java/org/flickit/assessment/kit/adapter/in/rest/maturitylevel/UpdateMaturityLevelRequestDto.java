package org.flickit.assessment.kit.adapter.in.rest.maturitylevel;

import org.flickit.assessment.common.application.domain.kit.translation.MaturityLevelTranslation;

import java.util.Map;

public record UpdateMaturityLevelRequestDto(Long kitId,
                                            String title,
                                            Integer index,
                                            String description,
                                            Integer value,
                                            Map<String, MaturityLevelTranslation> translations) {
}
