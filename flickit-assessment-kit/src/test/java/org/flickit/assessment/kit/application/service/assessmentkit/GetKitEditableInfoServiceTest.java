package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitEditableInfoPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_EDITABLE_INFO_KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetKitEditableInfoServiceTest {

    @InjectMocks
    private GetKitEditableInfoService service;
    @Mock
    private LoadKitEditableInfoPort loadKitEditableInfoPort;
    private final static Long KIT_ID = 1L;
    GetKitEditableInfoUseCase.Param param = new GetKitEditableInfoUseCase.Param(KIT_ID);

    @Test
    void testGetKitStats_KitNotFound_ErrorMessage() {
        when(loadKitEditableInfoPort.loadKitEditableInfo(KIT_ID)).thenThrow(new ResourceNotFoundException(GET_KIT_EDITABLE_INFO_KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.getKitEditableInfo(param));
        assertThat(throwable).hasMessage(GET_KIT_EDITABLE_INFO_KIT_ID_NOT_FOUND);
    }

    @Test
    void testGetKitStats_ValidInput_ValidResults() {
        GetKitEditableInfoUseCase.KitEditableInfo result = new GetKitEditableInfoUseCase.KitEditableInfo(
            1L,
            "Title",
            "Summary",
            Boolean.TRUE,
            0D,
            "About",
            List.of(new GetKitEditableInfoUseCase.KitEditableInfoTag(1L, "Tag1"))
        );

        when(loadKitEditableInfoPort.loadKitEditableInfo(KIT_ID)).thenReturn(result);

        GetKitEditableInfoUseCase.KitEditableInfo kitStats = service.getKitEditableInfo(param);

        assertEquals(result.id(), kitStats.id());
        assertEquals(result.title(), kitStats.title());
        assertEquals(result.summary(), kitStats.summary());
        assertEquals(result.isActive(), kitStats.isActive());
        assertEquals(result.price(), kitStats.price());
        assertEquals(result.about(), kitStats.about());
        assertEquals(result.tags().size(), kitStats.tags().size());
    }
}
