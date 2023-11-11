package org.flickit.assessment.kit.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AssessmentKit {

    private List<Questionnaire> questionnaires;

    private List<Attribute> attributes;

    private List<Question> questions;

    private List<Subject> subjects;

    private List<Level> levels;

    private boolean hasError;
}
