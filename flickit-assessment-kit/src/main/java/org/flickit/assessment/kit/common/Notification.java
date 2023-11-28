package org.flickit.assessment.kit.common;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class Notification {

    private final Set<Error> errors = new HashSet<>();

    public Notification add(Error error) {
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

    public record Error(String errorMessage, String... values) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Error error = (Error) o;
            return Objects.equals(errorMessage, error.errorMessage) && Arrays.equals(values, error.values);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(errorMessage);
            result = 31 * result + Arrays.hashCode(values);
            return result;
        }

        @Override
        public String toString() {
            return "Error{" +
                "values=" + Arrays.toString(values) +
                ", errorMessage=" + errorMessage +
                '}';
        }
    }
}
