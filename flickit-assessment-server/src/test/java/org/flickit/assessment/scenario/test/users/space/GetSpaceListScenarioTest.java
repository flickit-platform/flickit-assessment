package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.Map;

import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.junit.jupiter.api.Assertions.*;

class GetSpaceListScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    private final int pageSize = 5;

    @Test
    void getSpaceList() {
        int count = pageSize + 1;
        // Create spaces for the first user (will not be included in the test result)
        createSpaces(pageSize);
        // Switch to the next user and create actual test data
        context.getNextCurrentUser();
        createSpaces(count);
        // First page request
        Map<String, Integer> firstPageQueryParams = createQueryParam(0);
        var firstPageResponse = getPaginatedSpaces(firstPageQueryParams);
        // Second page request
        Map<String, Integer> secondPageQueryParams = createQueryParam(1);
        var secondPage = getPaginatedSpaces(secondPageQueryParams);
        // First page assertions
        assertEquals(pageSize, firstPageResponse.getSize());
        assertEquals(firstPageQueryParams.get("page"), firstPageResponse.getPage());
        assertEquals(firstPageQueryParams.get("size"), firstPageResponse.getSize());
        assertEquals(SpaceUserAccessJpaEntity.Fields.lastSeen, firstPageResponse.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), firstPageResponse.getOrder());
        assertEquals(count, firstPageResponse.getTotal());
        // Second page assertions
        assertEquals(1, secondPage.getItems().size());
        assertEquals(secondPageQueryParams.get("page"), secondPage.getPage());
        assertEquals(secondPageQueryParams.get("size"), secondPage.getSize());
        assertEquals(SpaceUserAccessJpaEntity.Fields.lastSeen, secondPage.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), secondPage.getOrder());
        assertEquals(count, secondPage.getTotal());
    }

    private void createSpaces(int count) {
        for (int i = 0; i < count; i++) {
            var createRequest = createSpaceRequestDto(b -> b.type(SpaceType.PREMIUM.getCode()));
            spaceHelper.create(context, createRequest)
                .then().statusCode(201);
        }
    }

    private Map<String, Integer> createQueryParam(int page) {
        return Map.of("page", page, "size", pageSize);
    }

    private PaginatedResponse getPaginatedSpaces(Map<String, Integer> queryParams) {
        return spaceHelper.getList(context, queryParams)
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(PaginatedResponse.class);
    }
}
