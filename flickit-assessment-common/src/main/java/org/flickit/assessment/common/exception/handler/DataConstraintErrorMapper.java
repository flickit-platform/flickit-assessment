package org.flickit.assessment.common.exception.handler;

public interface DataConstraintErrorMapper {

    boolean contains(String constraintName);

    String errorMessage(String constraintName);
}
