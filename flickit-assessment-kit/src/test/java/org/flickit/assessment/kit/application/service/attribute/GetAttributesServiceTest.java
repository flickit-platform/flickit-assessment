package org.flickit.assessment.kit.application.service.attribute;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.error.ErrorMessageKey;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributesUseCase;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributesUseCase.Param;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void testGetAttributes_UserHasNotAccess_ThrowsException() {
        Param param = new Param(15L, 0, 15, UUID.randomUUID());
        KitVersion kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getAttributes(param));
        assertEquals(ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verifyNoInteractions(loadAttributesPort);
    }

    @Test
    void testGetAttributes_ValidParam_ReturnsAttributes() {
        Param param = new Param(15L, 0, 15, UUID.randomUUID());
        KitVersion kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());
        Attribute attribute = AttributeMother.attributeWithTitle("atrribute");
        Subject subject = SubjectMother.subjectWithAttributes("subject", List.of(attribute));
        PaginatedResponse<Subject> paginatedResponse = new PaginatedResponse<>(
            List.of(subject),
            0,
            15,
            "index",
            "asc",
            1
        );
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAttributesPort.loadByKitVersionId(param.getKitVersionId(), param.getSize(), param.getPage())).thenReturn(paginatedResponse);

        PaginatedResponse<GetAttributesUseCase.SubjectListItem> result = service.getAttributes(param);

        assertEquals(paginatedResponse.getPage(), result.getPage());
        assertEquals(paginatedResponse.getOrder(), result.getOrder());
        assertEquals(paginatedResponse.getSort(), result.getSort());
        assertEquals(paginatedResponse.getTotal(), result.getTotal());

        var item = result.getItems().getFirst();
        assertEquals(subject.getId(), item.id());
        assertEquals(subject.getTitle(), item.title());

        GetAttributesUseCase.AttributeListItem attributeListItem = item.attributes().getFirst();
        assertEquals(attribute.getId(), attributeListItem.id());
        assertEquals(attribute.getIndex(), attributeListItem.index());
        assertEquals(attribute.getTitle(), attributeListItem.title());
        assertEquals(attribute.getDescription(), attributeListItem.description());
        assertEquals(attribute.getWeight(), attributeListItem.weight());
    }
}