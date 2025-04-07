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
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class GetSpaceListScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    private final int pageSize = 5;
    private final int secondPageExpertGroupCount = 1;
    private final int spaceCount = pageSize + secondPageExpertGroupCount;
    private int lastSpaceId = 0;

    @Test
    void getSpaceList() {
        // Create spaces for the first user (will not be included in the test result)
        createSpaces(pageSize);
        // Switch to the next user (main user) and create actual test data
        context.getNextCurrentUser();
        createSpaces(spaceCount);
        // First page request
        Map<String, Integer> firstPageQueryParams = createQueryParam(0);
        var firstPageResponse = getPaginatedSpaces(firstPageQueryParams);
        // Second page request
        Map<String, Integer> secondPageQueryParams = createQueryParam(1);
        var secondPageResponse = getPaginatedSpaces(secondPageQueryParams);
        // First page assertions
        assertPage(firstPageResponse, firstPageQueryParams, pageSize);
        // Second page assertions
        assertPage(secondPageResponse, secondPageQueryParams, secondPageExpertGroupCount);
    }

    @Test
    void getSpaceList_withDeletedSpace() {
        final int defaultPage = 0;
        final int defaultSize = 10;
        int count = pageSize + 1;
        // Create spaces
        createSpaces(count);
        // Delete the last created space
        spaceHelper.delete(context, lastSpaceId);
        // Page request with empty param
        var paginatedResponse = getPaginatedSpaces(Map.of());
        Map<String, Integer> secondPageQueryParams = createQueryParam(1);
        var secondPageResponse = getPaginatedSpaces(secondPageQueryParams);
        // Page assertions
        assertEquals(spaceCount - 1, paginatedResponse.getItems().size());
        assertEquals(defaultPage, paginatedResponse.getPage());
        assertEquals(defaultSize, paginatedResponse.getSize());
        assertEquals(SpaceUserAccessJpaEntity.Fields.lastSeen, paginatedResponse.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), paginatedResponse.getOrder());
        assertEquals(spaceCount - 1, paginatedResponse.getTotal());
        assertEquals(0, secondPageResponse.getItems().size());
    }

    private void createSpaces(int count) {
        for (int i = 0; i < count; i++) {
            var createRequest = createSpaceRequestDto(b -> b.type(SpaceType.PREMIUM.getCode()));
            var response = spaceHelper.create(context, createRequest)
                .then()
                .statusCode(201)
                .body("id", notNullValue());

            lastSpaceId = response.extract().path("id");
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

    private void assertPage(PaginatedResponse pageResponse, Map<String, Integer> firstPageQueryParams, int expectedSize) {
        assertEquals(expectedSize, pageResponse.getItems().size());
        assertEquals(firstPageQueryParams.get("page"), pageResponse.getPage());
        assertEquals(firstPageQueryParams.get("size"), pageResponse.getSize());
        assertEquals(SpaceUserAccessJpaEntity.Fields.lastSeen, pageResponse.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), pageResponse.getOrder());
        assertEquals(spaceCount, pageResponse.getTotal());
    }
}
