package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupUseCase.Member;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ExpertGroup {

    private final long id;
    private final String title;
    private final String bio;
    private final String about;
    private final String picture;
    private final String website;
    private final int membersCount;
    private final int publishedKitCount;
    private final List<Member> members;
    private final UUID ownerId;
}
