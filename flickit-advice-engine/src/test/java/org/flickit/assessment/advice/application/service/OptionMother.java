package org.flickit.assessment.advice.application.service;


import org.flickit.assessment.advice.application.domain.Option;
import org.flickit.assessment.advice.application.domain.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OptionMother {

    private static int id = 0;

    public static List<Option> createOptions(Target target) {
        int index = 1;
        List<Option> options = new ArrayList<>();
        options.add(createOption(index++, target, 0, 0, 10));
        options.add(createOption(index++, target, 2, 0.25, 10));
        options.add(createOption(index++, target, 4, 0.5, 10));
        options.add(createOption(index, target, 8, 1.0, 10));
        return options;
    }

    public static Option createOption(int index, Target target, double gain, double progress, int questionCost) {
        HashMap<Target, Double> gains = new HashMap<>();
        gains.put(target, gain);
        return new Option(id++, index, gains, progress, questionCost);
    }
}
