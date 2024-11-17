package org.flickit.assessment.kit.adapter.in.rest.kitcustom;

import java.util.List;

public record CreateKitCustomRequestDto(String title, CustomData customData) {

    public record CustomData(
        List<SubjectDto> subjects,
        List<AttributeDto> attributes,
        List<QuestionnaireDto> questionnaires) {

        public record SubjectDto(Long id, Integer weight) {}

        public record AttributeDto(Long id, Integer weight) {}

        public record QuestionnaireDto(Long id, Boolean disabled) {}
    }
}
