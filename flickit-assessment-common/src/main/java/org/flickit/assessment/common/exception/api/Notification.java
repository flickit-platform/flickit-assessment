package org.flickit.assessment.common.exception.api;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Notification {

    private final Set<Error> errors = new HashSet<>();

    public Notification add(Error error) {
        errors.add(error);
        return this;
    }

    public Notification add(String errorMsg) {
        errors.add(new SimpleError(errorMsg));
        return this;
    }

    public Notification merge(Notification notif) {
        notif.getErrors().forEach(this::add);
        return this;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public interface Error {
        String message();
    }

    public record SimpleError(String message) implements Error {
    }
}
