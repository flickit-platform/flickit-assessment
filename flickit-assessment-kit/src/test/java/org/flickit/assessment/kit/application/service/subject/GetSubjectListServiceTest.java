package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectListUseCase;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectListUseCase.Param;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.SubjectMother.subjectWithTitle;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSubjectListServiceTest {

    @InjectMocks
    GetSubjectListService service;

    @Mock
    LoadKitVersionPort loadKitVersionPort;

    @Mock
    CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    LoadSubjectsPort loadSubjectsPort;

    @Test
    void testGetSubjectListService_kitVersionNotExist_shouldThrowResourceNotFoundException() {
        Param param = createParam(GetSubjectListUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenThrow(new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getSubjectList(param));
        assertEquals(KIT_VERSION_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(checkExpertGroupAccessPort, loadSubjectsPort);
    }

    @Test
    void testGetSubjectListService_CurrentUserIsNotExpertGroupMember_shouldThrowAccessDeniedException() {
        Param param = createParam(GetSubjectListUseCase.Param.ParamBuilder::build);
        var assessmentKit = simpleKit();
        var kitVersion = createKitVersion(assessmentKit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getSubjectList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadSubjectsPort);
    }

    @Test
    void testGetSubjectListService_ValidParams_shouldReturnPaginatedSubjectList() {
        Param param = createParam(GetSubjectListUseCase.Param.ParamBuilder::build);
        var assessmentKit = simpleKit();
        var kitVersion = createKitVersion(assessmentKit);
        var subjectList = List.of(subjectWithTitle("title1"), subjectWithTitle("title2"));
        var paginatedResponse = new PaginatedResponse<>(subjectList,
            param.getPage(),
            param.getSize(),
            "index",
            "desc",
            4);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadSubjectsPort.loadPaginatedByKitVersionId(kitVersion.getId(), param.getPage(), param.getSize())).thenReturn(paginatedResponse);

        var result = service.getSubjectList(param);
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(paginatedResponse.getSort(), result.getSort());
        assertEquals(paginatedResponse.getSize(), result.getSize());
        assertEquals(paginatedResponse.getTotal(), result.getTotal());
        assertEquals(paginatedResponse.getOrder(), result.getOrder());
        assertEquals(paginatedResponse.getPage(), result.getPage());

        assertEquals(subjectList.size(), result.getItems().size());
        assertEquals(subjectList.getFirst().getId(), result.getItems().getFirst().id());
        assertEquals(subjectList.getFirst().getId(), result.getItems().getFirst().id());
        assertEquals(subjectList.getFirst().getIndex(), result.getItems().getFirst().index());
        assertEquals(subjectList.getFirst().getTitle(), result.getItems().getFirst().title());
        assertEquals(subjectList.getFirst().getDescription(), result.getItems().getFirst().description());
        assertEquals(subjectList.getFirst().getWeight(), result.getItems().getFirst().weight());

        assertEquals(subjectList.get(1).getId(), result.getItems().get(1).id());
        assertEquals(subjectList.get(1).getId(), result.getItems().get(1).id());
        assertEquals(subjectList.get(1).getIndex(), result.getItems().get(1).index());
        assertEquals(subjectList.get(1).getTitle(), result.getItems().get(1).title());
        assertEquals(subjectList.get(1).getDescription(), result.getItems().get(1).description());
        assertEquals(subjectList.get(1).getWeight(), result.getItems().get(1).weight());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
        return paramBuilder.build();
    }

    private GetSubjectListUseCase.Param.ParamBuilder paramBuilder() {
        return GetSubjectListUseCase.Param.builder()
            .kitVersionId(123L)
            .size(10)
            .page(1)
            .currentUserId(UUID.randomUUID());
    }
}
