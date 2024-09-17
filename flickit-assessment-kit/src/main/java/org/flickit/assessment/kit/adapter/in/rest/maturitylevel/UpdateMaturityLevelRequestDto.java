package org.flickit.assessment.kit.adapter.in.rest.maturitylevel;

public record UpdateMaturityLevelRequestDto(Long kitId,
                                            String title,
                                            Integer index,
                                            Integer value,
                                            String description) {
}
