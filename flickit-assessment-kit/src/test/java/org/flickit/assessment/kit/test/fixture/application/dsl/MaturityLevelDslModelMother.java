package org.flickit.assessment.kit.test.fixture.application.dsl;

import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toMap;

public class MaturityLevelDslModelMother {

    public static MaturityLevelDslModel domainToDslModel(MaturityLevel level) {
        return domainToDslModel(level, b -> {
        });
    }

    public static MaturityLevelDslModel domainToDslModel(MaturityLevel level,
                                                         Consumer<MaturityLevelDslModel.MaturityLevelDslModelBuilder<?, ?>> changer) {
        var builder = domainToDslModelBuilder(level);
        changer.accept(builder);
        return builder.build();
    }

    private static MaturityLevelDslModel.MaturityLevelDslModelBuilder<?, ?> domainToDslModelBuilder(MaturityLevel level) {
        return MaturityLevelDslModel.builder()
            .code(level.getCode())
            .title(level.getTitle())
            .description(level.getDescription())
            .index(level.getIndex())
            .value(level.getValue())
            .competencesCodeToValueMap(competenceListToMap(level.getCompetences()));
    }

    public static Map<String, Integer> competenceListToMap(List<MaturityLevelCompetence> competences) {
        if (competences == null)
            return Map.of();
        return competences.stream()
            .collect(toMap(x -> MaturityLevelMother.getCodeById(x.getEffectiveLevelId()), MaturityLevelCompetence::getValue));
    }
}
