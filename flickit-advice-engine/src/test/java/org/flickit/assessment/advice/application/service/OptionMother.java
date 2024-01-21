package org.flickit.assessment.advice.application.service;


import org.flickit.assessment.advice.application.domain.Option;
import org.flickit.assessment.advice.application.domain.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OptionMother {

    public static List<Option> createOptions(Target target) {
        List<Option> options = new ArrayList<>();
        options.add(createOption(target, 0, 0, 10));
        options.add(createOption(target, 2, 0.25, 10));
        options.add(createOption(target, 4, 0.5, 10));
        options.add(createOption(target, 8, 1.0, 10));
        return options;
    }

    public static Option createOption(Target target, double gain, double progress, int questionCost) {
        HashMap<Target, Double> gains = new HashMap<>();
        gains.put(target, gain);
        return new Option(gains, progress, questionCost);
    }
}
