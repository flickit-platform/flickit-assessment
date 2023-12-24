package org.flickit.assessment.kit.application.port.out.kituseraccess;

public interface DeleteKitUserAccessPort {

    void delete(Param param);

    record Param(Long kitId, String email) {
    }
}
