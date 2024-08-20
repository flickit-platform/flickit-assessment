package org.flickit.assessment.kit.application.service.assessmentkit.create;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.assessmentkit.CreateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkittag.CreateKitTagRelationPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupMemberIdsPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.LoadDslJsonPathPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.UpdateKitDslPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.GrantUserAccessToKitPort;
import org.flickit.assessment.kit.application.port.out.minio.LoadKitDSLJsonFilePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.error.ErrorMessageKey.FILE_STORAGE_FILE_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateKitByDslServiceTest {

    private static final Long EXPERT_GROUP_ID = 1L;
    private static final UUID EXPERT_GROUP_OWNER_ID = UUID.randomUUID();
    private static final Long KIT_DSL_ID = 1L;
    private static final UUID DSL_JSON_VERSION_ID = UUID.randomUUID();
    private static final String DSL_JSON = "sample/json/file/path/" + DSL_JSON_VERSION_ID;
    private static final Long KIT_ID = 1L;
    private static final Long KIT_VERSION_ID = 1L;
    private static final String TITLE = "title";
    private static final String SUMMARY = "summary";
    private static final String ABOUT = "about";
    private static final Long TAG_ID = 1L;

    @InjectMocks
    private CreateKitByDslService service;
    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    @Mock
    private LoadDslJsonPathPort loadDslJsonPathPort;
    @Mock
    private LoadKitDSLJsonFilePort loadKitDSLJsonFilePort;
    @Mock
    private CreateAssessmentKitPort createAssessmentKitPort;
    @Mock
    private CompositeCreateKitPersister persister;
    @Mock
    private CreateKitTagRelationPort createKitTagRelationPort;
    @Mock
    private UpdateKitDslPort updateKitDslPort;
    @Mock
    private GrantUserAccessToKitPort grantUserAccessToKitPort;
    @Mock
    private LoadExpertGroupMemberIdsPort loadExpertGroupMemberIdsPort;

    @Test
    void testCreateKitByDsl_ValidInputs_CreateAndSaveKit() {
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenReturn(EXPERT_GROUP_OWNER_ID);

        when(loadDslJsonPathPort.loadJsonPath(KIT_DSL_ID)).thenReturn(DSL_JSON);

        String dslContent = "{\"questionnaireModels\" : [ {\"code\" : \"CleanArchitecture\",\"index\" : 1,\"title\" : \"Clean Architecture\",\"description\" : \"desc\"}, {\"code\" : \"CodeQuality\",\"index\" : 2,    \"title\" : \"Code Quality\",\"description\" : \"desc\"} ],\"attributeModels\" : [ ],\"questionModels\" : [ ],\"subjectModels\" : [ {\"code\" : \"Software\",\"index\" : 1,\"title\" : \"Software\",\"description\" : \"desc\",\"weight\" : 0,\"questionnaireCodes\" : null}, {\"code\" : \"Team\",\"index\" : 2,\"title\" : \"Team\",\"description\" : \"desc\",\"weight\" : 0,\"questionnaireCodes\" : null} ],\"levelModels\" : [ ],\"hasError\" : false}";
        when(loadKitDSLJsonFilePort.loadDslJson(DSL_JSON)).thenReturn(dslContent);

        UUID currentUserId = EXPERT_GROUP_OWNER_ID;
        var kitCreateParam = new CreateAssessmentKitPort.Param(
            TITLE,
            TITLE,
            SUMMARY,
            ABOUT,
            Boolean.FALSE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            KitVersionStatus.ACTIVE,
            currentUserId);
        when(createAssessmentKitPort.persist(kitCreateParam)).thenReturn(new CreateAssessmentKitPort.Result(KIT_ID, KIT_VERSION_ID));

        doNothing().when(persister).persist(any(), eq(KIT_ID), eq(currentUserId));

        doNothing().when(updateKitDslPort).update(anyLong(), anyLong(), any(), any());

        UUID expertGroupMemberId = UUID.randomUUID();
        List<LoadExpertGroupMemberIdsPort.Result> expertGroupMembers = List.of(
            new LoadExpertGroupMemberIdsPort.Result(currentUserId),
            new LoadExpertGroupMemberIdsPort.Result(expertGroupMemberId));
        List<UUID> expertGroupMemberIds = List.of(currentUserId, expertGroupMemberId);
        when(loadExpertGroupMemberIdsPort.loadMemberIds(EXPERT_GROUP_ID)).thenReturn(expertGroupMembers);
        doNothing().when(grantUserAccessToKitPort).grantUsersAccess(KIT_ID, expertGroupMemberIds);

        var param = new CreateKitByDslUseCase.Param(TITLE, SUMMARY, ABOUT, Boolean.FALSE, KIT_DSL_ID, EXPERT_GROUP_ID, List.of(1L), currentUserId);
        Long savedKitId = service.create(param);

        assertEquals(KIT_ID, savedKitId);

        verify(createKitTagRelationPort, times(1)).persist(List.of(TAG_ID), KIT_ID);
    }

    @Test
    void testCreateKitByDsl_CurrentUserIsNotExpertGroupOwner_ThrowException() {
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenReturn(EXPERT_GROUP_OWNER_ID);

        UUID currentUserId = UUID.randomUUID();
        var param = new CreateKitByDslUseCase.Param(TITLE, SUMMARY, ABOUT, Boolean.FALSE, KIT_DSL_ID, EXPERT_GROUP_ID, List.of(1L), currentUserId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.create(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    @Test
    void testCreateKitByDsl_KitDslDoesNotExist_ThrowException() {
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenReturn(EXPERT_GROUP_OWNER_ID);

        when(loadDslJsonPathPort.loadJsonPath(KIT_DSL_ID)).thenThrow(new ResourceNotFoundException(CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND));

        var param = new CreateKitByDslUseCase.Param(
            TITLE,
            SUMMARY,
            ABOUT,
            Boolean.FALSE,
            KIT_DSL_ID,
            EXPERT_GROUP_ID,
            List.of(1L),
            EXPERT_GROUP_OWNER_ID);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.create(param));
        assertThat(throwable).hasMessage(CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND);
    }

    @Test
    void testCreateKitByDsl_JsonFileIsNotUploaded_ThrowException() {
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenReturn(EXPERT_GROUP_OWNER_ID);

        when(loadDslJsonPathPort.loadJsonPath(KIT_DSL_ID)).thenReturn(DSL_JSON);

        when(loadKitDSLJsonFilePort.loadDslJson(any())).thenThrow(new ResourceNotFoundException(FILE_STORAGE_FILE_NOT_FOUND));

        var param = new CreateKitByDslUseCase.Param(
            TITLE,
            SUMMARY,
            ABOUT,
            Boolean.FALSE,
            KIT_DSL_ID,
            EXPERT_GROUP_ID,
            List.of(1L),
            EXPERT_GROUP_OWNER_ID);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.create(param));
        assertThat(throwable).hasMessage(FILE_STORAGE_FILE_NOT_FOUND);
    }
}
