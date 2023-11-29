package org.flickit.assessment.kit.test.fixture.application.dsl.model.mother;

import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;

import java.util.Map;

public class MaturityLevelDslModelMother {

    public static MaturityLevelDslModel maturityLevelDslModel(
        String code,
        Integer index,
        String title,
        String description,
        Map<String, Integer> competencesCodeToValueMap,
        Integer value) {
        MaturityLevelDslModel dslMaturityLevel = new MaturityLevelDslModel();
        dslMaturityLevel.setCode(code);
        dslMaturityLevel.setIndex(index);
        dslMaturityLevel.setTitle(title);
        dslMaturityLevel.setDescription(description);
        dslMaturityLevel.setCompetencesCodeToValueMap(competencesCodeToValueMap);
        dslMaturityLevel.setValue(value);
        return dslMaturityLevel;
    }
}
