package org.flickit.assessment.data.jpa.kit.seq;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.infra.db.seq.api.SequenceGenerator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KitDbSequenceGenerators {

    private final SequenceGenerator sequenceGenerator;

    public Long generateAnswerOptionId() {
        return sequenceGenerator.generate("fak_answer_option_id_seq");
    }

    public Long generateAnswerOptionImpactId() {
        return sequenceGenerator.generate("fak_answer_option_impact_id_seq");
    }

    public Long generateKitId() {
        return sequenceGenerator.generate("fak_assessment_kit_id_seq");
    }

    public Long generateKitDslId() {
        return sequenceGenerator.generate("fak_kit_dsl_id_seq");
    }

    public Long generateAttributeId() {
        return sequenceGenerator.generate("fak_attribute_id_seq");
    }

    public Long generateKitTagId() {
        return sequenceGenerator.generate("fak_kit_tag_id_seq");
    }

    public Long generateKitVersionId() {
        return sequenceGenerator.generate("fak_kit_version_id_seq");
    }

    public Long generateLevelCompetenceId() {
        return sequenceGenerator.generate("fak_level_competence_id_seq");
    }

    public Long generateMaturityLevelId() {
        return sequenceGenerator.generate("fak_maturity_level_id_seq");
    }

    public Long generateQuestionId() {
        return sequenceGenerator.generate("fak_question_id_seq");
    }

    public Long generateQuestionImpactId() {
        return sequenceGenerator.generate("fak_question_impact_id_seq");
    }

    public Long generateQuestionnaireId() {
        return sequenceGenerator.generate("fak_questionnaire_id_seq");
    }

    public Long generateSubjectId() {
        return sequenceGenerator.generate("fak_subject_id_seq");
    }

    public Long generateSubjectQuestionnaireId() {
        return sequenceGenerator.generate("fak_subject_questionnaire_id_seq");
    }

    public Long generateAnswerRangeId() {
        return sequenceGenerator.generate("fak_answer_range_id_seq");
    }

    public Long generateKitCustomId() {
        return sequenceGenerator.generate("fak_kit_custom_id_seq");
    }
}
