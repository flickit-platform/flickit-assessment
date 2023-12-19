package org.flickit.assessment.kit.application.port.out.useraccess;

public interface DeleteUserAccessPort {

    void delete(Param param);

    record Param(Long kitId, String email) {
    }
}
