package org.flickit.assessment.advice.test.fixture.application;


import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OptionMother {

    private static int id = 0;

    public static List<Option> createOptions(AttributeLevelScore attributeLevelScore) {
        int index = 1;
        List<Option> options = new ArrayList<>();
        options.add(createOption(index++, attributeLevelScore, 0, 0, 10));
        options.add(createOption(index++, attributeLevelScore, 2, 0.25, 10));
        options.add(createOption(index++, attributeLevelScore, 4, 0.5, 10));
        options.add(createOption(index, attributeLevelScore, 8, 1.0, 10));
        return options;
    }

    public static Option createOption(int index, AttributeLevelScore attributeLevelScore, double gain, double progress, int questionCost) {
        HashMap<AttributeLevelScore, Double> gains = new HashMap<>();
        gains.put(attributeLevelScore, gain);
        return new Option(id++, index, gains, progress, questionCost);
    }
}
