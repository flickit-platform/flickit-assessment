package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.flickit.assessment.common.application.domain.kit.translation.KitTranslation;

import java.util.List;
import java.util.Map;

@Builder
public record UpdateKitInfoRequestDto(@JsonIgnoreProperties(ignoreUnknown = true) String title,
                                      @JsonIgnoreProperties(ignoreUnknown = true) String summary,
                                      @JsonIgnoreProperties(ignoreUnknown = true) String lang,
                                      @JsonIgnoreProperties(ignoreUnknown = true) Boolean published,
                                      @JsonIgnoreProperties(ignoreUnknown = true) Boolean isPrivate,
                                      @JsonIgnoreProperties(ignoreUnknown = true) Double price,
                                      @JsonIgnoreProperties(ignoreUnknown = true) String about,
                                      @JsonIgnoreProperties(ignoreUnknown = true) List<Long> tags,
                                      @JsonIgnoreProperties(ignoreUnknown = true) Map<String, KitTranslation> translations,
                                      @JsonIgnoreProperties(ignoreUnknown = true) boolean removeTranslations,
                                      @JsonIgnoreProperties(ignoreUnknown = true) String metadata) {
}
