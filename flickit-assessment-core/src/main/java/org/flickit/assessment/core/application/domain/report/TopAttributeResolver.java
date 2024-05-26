package org.flickit.assessment.core.application.domain.report;

import lombok.Value;
import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.util.List;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

@Value
public class TopAttributeResolver {

    private static final int TOP_COUNT = 3;

    List<AttributeReportItem> attributes;
    MaturityLevel midLevelMaturity;

    public List<TopAttribute> getTopStrengths() {
        return attributes.stream()
            .sorted(comparing(AttributeReportItem::index, reverseOrder()))
            .filter(x -> isHigherThanOrEqualToMiddleLevel(x.maturityLevel().getIndex()))
            .limit(TOP_COUNT)
            .map(x -> new TopAttribute(x.id(), x.title()))
            .toList();
    }

    private boolean isHigherThanOrEqualToMiddleLevel(int maturityLevelIndex) {
        return maturityLevelIndex >= midLevelMaturity.getIndex();
    }

    public List<TopAttribute> getTopWeaknesses() {
        return attributes.stream()
            .sorted(comparingInt(AttributeReportItem::index))
            .filter(x -> isLowerThanMiddleLevel(x.maturityLevel().getIndex()))
            .limit(TOP_COUNT)
            .map(x -> new TopAttribute(x.id(), x.title()))
            .toList();
    }

    private boolean isLowerThanMiddleLevel(int maturityLevelIndex) {
        return maturityLevelIndex < midLevelMaturity.getIndex();
    }
}
