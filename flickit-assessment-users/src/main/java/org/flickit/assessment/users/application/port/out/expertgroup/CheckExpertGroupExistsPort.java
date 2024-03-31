package org.flickit.assessment.users.application.port.out.expertgroup;

public interface CheckExpertGroupExistsPort {

    boolean existsByIdAndDeletedFalse(long id);
}
