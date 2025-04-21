package org.flickit.assessment.scenario.test.users.space;

import io.restassured.common.mapper.TypeRef;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.users.application.port.in.space.GetSpaceListUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class GetSpaceListScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    private final int pageSize = 5;
    private final int secondPageSpaceCount = 1;
    private final int spaceCount = pageSize + secondPageSpaceCount;
    private Long lastSpaceId = 0L;

    @Test
    void getSpaceList() {
        // Create spaces for the first user (will not be included in the test result)
        createSpaces(pageSize);
        // Switch to the next user (main user) and create actual test data
        context.getNextCurrentUser();
        var createdSpaces = createSpaces(spaceCount);
        // First page request
        Map<String, Integer> firstPageQueryParams = createQueryParam(0);
        var firstPageResponse = getPaginatedSpaces(firstPageQueryParams);
        // Second page request
        Map<String, Integer> secondPageQueryParams = createQueryParam(1);
        var secondPageResponse = getPaginatedSpaces(secondPageQueryParams);
        // First page assertions
        assertPageProperties(firstPageResponse, firstPageQueryParams, pageSize);
        //assertPageItems(firstPageResponse.getItems(), createdSpaces);
        List<GetSpaceListUseCase.SpaceListItem> firstPageExpectedItems = createdSpaces
            .reversed()
            .subList(0, pageSize).stream()
            .map(this::convertToSpaceListItem).toList();

        assertPageItems(firstPageResponse, firstPageExpectedItems);
        // Second page assertions
        assertPageProperties(secondPageResponse, secondPageQueryParams, secondPageSpaceCount);
        List<GetSpaceListUseCase.SpaceListItem> secondPageExpectedItems = createdSpaces
            .reversed()
            .subList(pageSize, createdSpaces.size()).stream()
            .map(this::convertToSpaceListItem).toList();

        assertPageItems(secondPageResponse, secondPageExpectedItems);
    }

    private static void assertPageItems(PaginatedResponse<GetSpaceListUseCase.SpaceListItem> pageResponse, List<GetSpaceListUseCase.SpaceListItem> pageExpectedItems) {
        assertThat(pageResponse.getItems())
            .zipSatisfy(pageExpectedItems, (actual, expected) -> {
                assertEquals(expected.id(), actual.id());
            });
    }

    GetSpaceListUseCase.SpaceListItem convertToSpaceListItem(Long spaceId) {
        return new GetSpaceListUseCase.SpaceListItem(spaceId,null,null,null,true, null,0,0);
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

    private LinkedList<Long> createSpaces(int count) {
        LinkedList<Long> spaceIds = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            var createRequest = createSpaceRequestDto(b -> b.type(SpaceType.PREMIUM.getCode()));
            var response = spaceHelper.create(context, createRequest)
                .then()
                .statusCode(201)
                .body("id", notNullValue());

            lastSpaceId = ((Number) response.extract().path("id")).longValue();
            spaceIds.add(lastSpaceId);
        }
        return spaceIds;
    }

    private Map<String, Integer> createQueryParam(int page) {
        return Map.of("page", page, "size", pageSize);
    }

    private PaginatedResponse<GetSpaceListUseCase.SpaceListItem> getPaginatedSpaces(Map<String, Integer> queryParams) {
        return spaceHelper.getList(context, queryParams)
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(new TypeRef<>() {});
    }

    private void assertPageProperties(PaginatedResponse pageResponse, Map<String, Integer> queryParams, int expectedSize) {
        assertEquals(expectedSize, pageResponse.getItems().size());
        assertEquals(queryParams.get("page"), pageResponse.getPage());
        assertEquals(queryParams.get("size"), pageResponse.getSize());
        assertEquals(SpaceUserAccessJpaEntity.Fields.lastSeen, pageResponse.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), pageResponse.getOrder());
        assertEquals(spaceCount, pageResponse.getTotal());
    }
}
