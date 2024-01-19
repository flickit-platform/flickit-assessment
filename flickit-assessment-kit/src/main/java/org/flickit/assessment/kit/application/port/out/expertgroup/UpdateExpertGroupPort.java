package org.flickit.assessment.kit.application.port.out.expertgroup;

import java.util.UUID;

public interface UpdateExpertGroupPort {

    void update(Param param);

    record Param(Long id,
                 String title,
                 String bio,
                 String about,
                 String website,
                 String picture,
                 UUID owner_id) {
    }
}
