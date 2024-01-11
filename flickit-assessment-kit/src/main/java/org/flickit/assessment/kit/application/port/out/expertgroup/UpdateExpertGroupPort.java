package org.flickit.assessment.kit.application.port.out.expertgroup;

import java.util.UUID;

public interface UpdateExpertGroupPort {

    void update (Param param);

    record Param(Long id,
                 String title,
                 String about,
                 String picture,
                 String website,
                 String bio,
                 UUID owner_id){
    }
}
