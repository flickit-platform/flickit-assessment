package org.flickit.assessment.kit.application.port.out.expertgroup;

import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupUseCase;

import java.util.List;
import java.util.UUID;

public interface LoadExpertGroupPort {

    Result loadExpertGroup(Param param);

    record Param(Long id) {
    }

    record Result(Long id, String title, String bio,String about, String picture, String website, Integer publishedKitsCount,
                  Integer membersCount, List<GetExpertGroupUseCase.Member> members, UUID ownerId) {
    }
}
