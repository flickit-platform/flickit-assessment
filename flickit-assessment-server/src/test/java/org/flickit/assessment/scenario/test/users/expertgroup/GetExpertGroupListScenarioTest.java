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

    private int lastExpertGroupId = 0;

    int expertGroupCount = pageSize + 1;

    @Test
    void getExpertGroupList() {
        // Create expert groups for the first user (will not be included in the test result)
        createExpertGroups(1);
        // Switch to the next user (main user) and create actual test data
        context.getNextCurrentUser();
        createExpertGroups(expertGroupCount);
        // First page assertions
        Map<String, Integer> firstPageQueryParams = createQueryParam(0);
        var firstPageResponse = getPaginatedExpertGroups(firstPageQueryParams);
        // First page assertions
        assertPage(firstPageResponse, firstPageQueryParams);
        // Second page request
        Map<String, Integer> secondPageQueryParams = createQueryParam(1);
        var secondPageResponse = getPaginatedExpertGroups(secondPageQueryParams);
        // Second page assertions
        assertPage(secondPageResponse, secondPageQueryParams);
    }

    @Test
    void getExpertGroupList_withDeletedExpertGroup() {
        final int defaultPage = 0;
        final int defaultSize = 10;
        // Create expert groups
        createExpertGroups(expertGroupCount);
        // Delete the last created space
        expertGroupHelper.delete(context, lastExpertGroupId);
        // Page request with empty param
        var paginatedResponse = getPaginatedExpertGroups(Map.of());
        // Page assertions
        assertEquals(expertGroupCount - 1, paginatedResponse.getItems().size());
        assertEquals(defaultSize, paginatedResponse.getSize());
        assertEquals(defaultPage, paginatedResponse.getPage());
        assertEquals(ExpertGroupAccessJpaEntity.Fields.lastSeen, paginatedResponse.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), paginatedResponse.getOrder());
        assertEquals(expertGroupCount - 1, paginatedResponse.getTotal());
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

    private void assertPage(PaginatedResponse firstPageResponse, Map<String, Integer> firstPageQueryParams) {
        assertEquals(pageSize, firstPageResponse.getItems().size());
        assertEquals(pageSize, firstPageResponse.getSize());
        assertEquals(firstPageQueryParams.get("page"), firstPageResponse.getPage());
        assertEquals(firstPageQueryParams.get("size"), firstPageResponse.getSize());
        assertEquals(ExpertGroupAccessJpaEntity.Fields.lastSeen, firstPageResponse.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), firstPageResponse.getOrder());
        assertEquals(expertGroupCount, firstPageResponse.getTotal());
    }
}
