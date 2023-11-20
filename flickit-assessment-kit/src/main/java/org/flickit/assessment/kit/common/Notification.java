package org.flickit.assessment.kit.common;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Notification {

    private final Set<String> errors = new HashSet<>();

    public Notification add(String error) {
        errors.add(error);
        return this;
    }

    public Notification merge(Notification notif) {
        notif.getErrors().forEach(this::add);
        return this;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
