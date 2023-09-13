package org.flickit.flickitassessmentcore.common.report;

import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;

import java.util.List;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

public class EntityReportCommonCalculations {

    private EntityReportCommonCalculations() {
    }

    private static final int TOP_COUNT = 3;

    public static List<Long> getTopStrengths(List<QualityAttributeValue> attributeValues, MaturityLevel midLevelMaturity) {
        return attributeValues.stream()
            .sorted(comparing(x -> x.getMaturityLevel().getLevel(), reverseOrder()))
            .filter(x -> isHigherThanOrEqualToMiddleLevel(x.getMaturityLevel(), midLevelMaturity))
            .limit(TOP_COUNT)
            .map(x -> x.getQualityAttribute().getId())
            .toList();
    }

    private static boolean isHigherThanOrEqualToMiddleLevel(MaturityLevel maturityLevel, MaturityLevel midLevelMaturity) {
        return maturityLevel.getLevel() >= midLevelMaturity.getLevel();
    }

    public static List<Long> getTopWeaknesses(List<QualityAttributeValue> attributeValues, MaturityLevel midLevelMaturity) {
        return attributeValues.stream()
            .sorted(comparingInt(x -> x.getMaturityLevel().getLevel()))
            .filter(x -> isLowerThanMiddleLevel(x.getMaturityLevel(), midLevelMaturity))
            .limit(TOP_COUNT)
            .map(x -> x.getQualityAttribute().getId())
            .toList();
    }

    private static boolean isLowerThanMiddleLevel(MaturityLevel maturityLevel, MaturityLevel midLevelMaturity) {
        return maturityLevel.getLevel() < midLevelMaturity.getLevel();
    }

    public static MaturityLevel middleLevel(List<MaturityLevel> maturityLevels) {
        var sortedMaturityLevels = maturityLevels.stream()
            .sorted(comparingInt(MaturityLevel::getLevel))
            .toList();
        return sortedMaturityLevels.get((sortedMaturityLevels.size() / 2));
    }
}
