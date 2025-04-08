package org.flickit.assessment.scenario.test.users.expertgroup;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.Map;

import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GetExpertGroupListScenarioTest extends AbstractScenarioTest {

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    private final int pageSize = 5;
    private final int secondPageExpertGroupCount = 1;
    private final int expertGroupsCount = pageSize + secondPageExpertGroupCount;
    private int lastExpertGroupId = 0;

    @Test
    void getExpertGroupList() {
        // Create one expert group for the first user (will not be included in the test result)
        createExpertGroups(1);
        // Switch to the next user (main user) and create actual test data
        context.getNextCurrentUser();
        createExpertGroups(expertGroupsCount);
        // First page assertions
        Map<String, Integer> firstPageQueryParams = createQueryParam(0);
        var firstPageResponse = getPaginatedExpertGroups(firstPageQueryParams);
        // Second page request
        Map<String, Integer> secondPageQueryParams = createQueryParam(1);
        var secondPageResponse = getPaginatedExpertGroups(secondPageQueryParams);
        // First page assertions
        assertPage(firstPageResponse, firstPageQueryParams, pageSize);
        // Second page assertions
        assertPage(secondPageResponse, secondPageQueryParams, secondPageExpertGroupCount);
    }

    @Test
    void getExpertGroupList_withDeletedExpertGroup() {
        final int defaultPage = 0;
        final int defaultSize = 10;
        // Create expert groups
        createExpertGroups(expertGroupsCount);
        // Delete the last created expert group
        expertGroupHelper.delete(context, lastExpertGroupId);
        // Page request with empty param
        var firstPageResponse = getPaginatedExpertGroups(Map.of());
        // Second page request
        Map<String, Integer> secondPageQueryParams = createQueryParam(1);
        var secondPageResponse = getPaginatedExpertGroups(secondPageQueryParams);
        // Page assertions
        assertEquals(expertGroupsCount - 1, firstPageResponse.getItems().size());
        assertEquals(defaultSize, firstPageResponse.getSize());
        assertEquals(defaultPage, firstPageResponse.getPage());
        assertEquals(ExpertGroupAccessJpaEntity.Fields.lastSeen, firstPageResponse.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), firstPageResponse.getOrder());
        assertEquals(expertGroupsCount - 1, firstPageResponse.getTotal());
        assertEquals(0, secondPageResponse.getItems().size());
    }

    private void createExpertGroups(int count) {
        for (int i = 0; i < count; i++) {
            var createRequest = createExpertGroupRequestDto();
            var response = expertGroupHelper.create(context, createRequest)
                .then()
                .statusCode(201)
                .body("id", notNullValue());

            lastExpertGroupId = response.extract().path("id");
        }
    }

    private Map<String, Integer> createQueryParam(int page) {
        return Map.of("page", page, "size", pageSize);
    }

    private PaginatedResponse getPaginatedExpertGroups(Map<String, Integer> queryParams) {
        return expertGroupHelper.getList(context, queryParams)
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(PaginatedResponse.class);
    }

    private void assertPage(PaginatedResponse pageResponse, Map<String, Integer> queryParams, int expectedSize) {
        assertEquals(expectedSize, pageResponse.getItems().size());
        assertEquals(queryParams.get("page"), pageResponse.getPage());
        assertEquals(queryParams.get("size"), pageResponse.getSize());
        assertEquals(ExpertGroupAccessJpaEntity.Fields.lastSeen, pageResponse.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), pageResponse.getOrder());
        assertEquals(expertGroupsCount, pageResponse.getTotal());
    }
}
