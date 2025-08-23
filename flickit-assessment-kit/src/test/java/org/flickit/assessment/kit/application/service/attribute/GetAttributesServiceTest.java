package org.flickit.assessment.kit.application.service.attribute;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.translation.AttributeTranslation;
import org.flickit.assessment.common.error.ErrorMessageKey;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AttributeWithSubject;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributesUseCase.Param;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.kit.KitLanguage.FA;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.AttributeMother.attributeWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.SubjectMother.subjectWithTitle;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttributesServiceTest {

    @InjectMocks
    private GetAttributesService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadAttributesPort loadAttributesPort;

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testGetAttributes_whenCurrentUserDoesNotHasAccess_thenThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getAttributes(param));
        assertEquals(ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(loadAttributesPort);
    }

    @Test
    void testGetAttributes_whenCurrentUserHasAccess_thenReturnAttributes() {
        var param = createParam(Param.ParamBuilder::build);

        var subject = subjectWithTitle("subject");
        var attribute1 = attributeWithTitle("agility");
        attribute1.setTranslations(Map.of(FA, new AttributeTranslation("fa-title", "fa-desc")));

        var attribute2 = attributeWithTitle("performance");

        PaginatedResponse<AttributeWithSubject> paginatedResponse = new PaginatedResponse<>(
            List.of(new AttributeWithSubject(attribute1, subject),
                new AttributeWithSubject(attribute2, subject)),
            0,
            15,
            "index",
            "asc",
            2
        );

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAttributesPort.loadByKitVersionId(param.getKitVersionId(), param.getSize(), param.getPage())).thenReturn(paginatedResponse);

        var result = service.getAttributes(param);

        assertEquals(paginatedResponse.getPage(), result.getPage());
        assertEquals(paginatedResponse.getOrder(), result.getOrder());
        assertEquals(paginatedResponse.getSort(), result.getSort());
        assertEquals(paginatedResponse.getTotal(), result.getTotal());

        assertThat(result.getItems())
            .zipSatisfy(paginatedResponse.getItems(), (actual, expected) -> {
                assertEquals(expected.attribute().getId(), actual.id());
                assertEquals(expected.attribute().getIndex(), actual.index());
                assertEquals(expected.attribute().getTitle(), actual.title());
                assertEquals(expected.attribute().getDescription(), actual.description());
                assertEquals(expected.attribute().getWeight(), actual.weight());

                assertEquals(expected.subject().getId(), actual.subject().id());
                assertEquals(expected.subject().getTitle(), actual.subject().title());
            });

        var attribute1Translations = result.getItems().getFirst().translations();
        assertEquals(1, attribute1Translations.size());
        assertNotNull(attribute1Translations.get(FA));
        assertEquals(attribute1.getTranslations().get(FA).title(), attribute1Translations.get(FA).title());
        assertEquals(attribute1.getTranslations().get(FA).description(), attribute1Translations.get(FA).description());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .size(10)
            .page(2)
            .currentUserId(UUID.randomUUID());
    }
}
