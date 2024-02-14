package org.flickit.assessment.kit.application.port.out.expertgroup;

import java.util.UUID;

public interface LoadExpertGroupPort {

    Result loadExpertGroup(Long id);

    record Result(Long id, String title, String bio, String about, String picture, String website, UUID ownerId) {
    }
}
