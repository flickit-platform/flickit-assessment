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
import org.springframework.data.jpa.domain.Specification;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        // Assert first page item ids
        List<Long> firstPageExpectedItemIds = createdSpaces
            .reversed()
            .subList(0, pageSize).stream()
            .toList();
        List<Long> firstPageActualIds = firstPageResponse.getItems().stream()
            .map(GetSpaceListUseCase.SpaceListItem::id)
            .toList();

        assertEquals(firstPageExpectedItemIds, firstPageActualIds);

        // Second page assertions
        assertPageProperties(secondPageResponse, secondPageQueryParams, secondPageSpaceCount);
        // Assert second page item ids
        List<Long> secondPageExpectedItemsIds = createdSpaces
            .reversed()
            .subList(pageSize, createdSpaces.size()).stream()
            .toList();
        List<Long> secondPageActualIds = secondPageResponse.getItems().stream()
            .map(GetSpaceListUseCase.SpaceListItem::id)
            .toList();

        assertEquals(secondPageExpectedItemsIds, secondPageActualIds);

        // Assert order of items according to LastSeen
        Specification<SpaceUserAccessJpaEntity> matchAllSpec = (root, query, cb) -> cb.conjunction();
        List<Long> expectedSortedSpaceIds = jpaTemplate.search(SpaceUserAccessJpaEntity.class, matchAllSpec).stream()
            .sorted(Comparator.comparing(SpaceUserAccessJpaEntity::getLastSeen).reversed())
            .map(SpaceUserAccessJpaEntity::getSpaceId)
            .toList()
            .subList(0, pageSize);

        assertEquals(expectedSortedSpaceIds, firstPageActualIds);
    }

    @Test
    void getSpaceList_withDeletedSpace() {
        final int defaultPage = 0;
        final int defaultSize = 10;
        int count = pageSize + 1;
        // Create spaces
        var createdSpaces = createSpaces(count);
        // Delete the last created space
        spaceHelper.delete(context, lastSpaceId);
        createdSpaces.removeLast();
        // Page request with empty param
        var firstPageResponse = getPaginatedSpaces(Map.of());
        Map<String, Integer> secondPageQueryParams = createQueryParam(1);
        var secondPageResponse = getPaginatedSpaces(secondPageQueryParams);

        // First Page assertions
        assertEquals(spaceCount - 1, firstPageResponse.getItems().size());
        assertEquals(defaultPage, firstPageResponse.getPage());
        assertEquals(defaultSize, firstPageResponse.getSize());
        assertEquals(SpaceUserAccessJpaEntity.Fields.lastSeen, firstPageResponse.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), firstPageResponse.getOrder());
        assertEquals(spaceCount - 1, firstPageResponse.getTotal());
        assertEquals(0, secondPageResponse.getItems().size());

        // Assert first page item ids
        List<Long> firstPageExpectedItemIds = createdSpaces
            .reversed()
            .subList(0, pageSize).stream()
            .toList();
        List<Long> firstPageActualIds = firstPageResponse.getItems().stream()
            .map(GetSpaceListUseCase.SpaceListItem::id)
            .toList();

        assertEquals(firstPageExpectedItemIds, firstPageActualIds);

        // Second Page assertions
        assertEquals(0, secondPageResponse.getItems().size());
        assertEquals(secondPageQueryParams.get("page"), secondPageResponse.getPage());
        assertEquals(secondPageQueryParams.get("size"), secondPageResponse.getSize());
        assertEquals(SpaceUserAccessJpaEntity.Fields.lastSeen, secondPageResponse.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), secondPageResponse.getOrder());
        assertEquals(count - 1, secondPageResponse.getTotal());
        assertEquals(0, secondPageResponse.getItems().size());
        // Assert second page item ids
        List<Long> secondPageExpectedItemsIds = createdSpaces
            .reversed()
            .subList(pageSize, createdSpaces.size()).stream()
            .toList();
        List<Long> secondPageActualIds = secondPageResponse.getItems().stream()
            .map(GetSpaceListUseCase.SpaceListItem::id)
            .toList();

        assertEquals(secondPageExpectedItemsIds, secondPageActualIds);
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
            .as(new TypeRef<>() {
            });
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
