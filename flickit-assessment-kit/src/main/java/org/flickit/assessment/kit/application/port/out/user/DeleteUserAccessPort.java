package org.flickit.assessment.kit.application.port.out.user;

public interface DeleteUserAccessPort {

    void delete(Param param);

    record Param(Long kitId, Long userId) {
    }
}
