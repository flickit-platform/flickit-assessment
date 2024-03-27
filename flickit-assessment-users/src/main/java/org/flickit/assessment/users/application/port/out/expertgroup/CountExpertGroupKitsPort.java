package org.flickit.assessment.users.application.port.out.expertgroup;

public interface CountExpertGroupKitsPort {

    Result countKits(long expertGroupId);

    record Result(int publishedKitsCount, int unpublishedKitsCount) {
    }
}
