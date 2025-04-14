package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase.KitEditableInfo.Language;

import java.util.List;

public record GetKitEditableInfoResponseDto(Long id,
                                            String title,
                                            String summary,
                                            String lang,
                                            Boolean published,
                                            Boolean isPrivate,
                                            Double price,
                                            String about,
                                            List<KitTag> tags,
                                            boolean editable,
                                            boolean hasActiveVersion,
                                            List<Language> languages) {
}
