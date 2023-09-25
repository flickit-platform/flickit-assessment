package org.flickit.flickitassessmentcore.application.domain.report;

import lombok.Value;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;

import java.util.List;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

@Value
public class TopAttributeResolver {

    private static final int TOP_COUNT = 3;

    List<QualityAttributeValue> attributeValues;
    MaturityLevel midLevelMaturity;

    public List<TopAttribute> getTopStrengths() {
        return attributeValues.stream()
            .sorted(comparing(x -> x.getMaturityLevel().getLevel(), reverseOrder()))
            .filter(x -> isHigherThanOrEqualToMiddleLevel(x.getMaturityLevel()))
            .limit(TOP_COUNT)
            .map(x -> new TopAttribute(x.getQualityAttribute().getId()))
            .toList();
    }

    private boolean isHigherThanOrEqualToMiddleLevel(MaturityLevel maturityLevel) {
        return maturityLevel.getLevel() >= midLevelMaturity.getLevel();
    }

    public List<TopAttribute> getTopWeaknesses() {
        return attributeValues.stream()
            .sorted(comparingInt(x -> x.getMaturityLevel().getLevel()))
            .filter(x -> isLowerThanMiddleLevel(x.getMaturityLevel()))
            .limit(TOP_COUNT)
            .map(x -> new TopAttribute(x.getQualityAttribute().getId()))
            .toList();
    }

    private boolean isLowerThanMiddleLevel(MaturityLevel maturityLevel) {
        return maturityLevel.getLevel() < midLevelMaturity.getLevel();
    }
}
