package org.flickit.flickitassessmentcore.adapter.in.rest.evidence;

import org.flickit.flickitassessmentcore.adapter.out.persistence.evidence.EvidenceJpaEntity;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.domain.mother.EvidenceMother;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GetEvidenceListRestController.class)
class GetEvidenceListRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetEvidenceListUseCase useCase;

    @Test
    void testGetEvidenceList() throws Exception {
        var assessmentId = UUID.randomUUID();
        var evidence1 = EvidenceMother.evidenceListItem(assessmentId);
        var evidence2 = EvidenceMother.evidenceListItem(assessmentId);
        var evidence3 = EvidenceMother.evidenceListItem(assessmentId);
        var evidences = List.of(evidence1, evidence2, evidence3);
        var questionId = 1L;
        var size = 10;
        var page = 0;
        var sort = EvidenceJpaEntity.Fields.LAST_MODIFICATION_TIME;
        var order = Sort.Direction.DESC.name().toLowerCase();

        var param = new GetEvidenceListUseCase.Param(questionId, assessmentId, size, page);
        var paginatedResponse = new PaginatedResponse<>(evidences, page, size, sort, order, evidences.size());
        when(useCase.getEvidenceList(param)).thenReturn(paginatedResponse);

        mockMvc.perform(
                get("/evidences")
                    .param("questionId", String.valueOf(questionId))
                    .param("assessmentId", assessmentId.toString())
                    .param("size", String.valueOf(size))
                    .param("page", String.valueOf(page))
                    .header("Content-Type", "application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.items").exists())
            .andExpect(jsonPath("$.items[0].id").value(evidence1.id().toString()))
            .andExpect(jsonPath("$.items[1].id").value(evidence2.id().toString()))
            .andExpect(jsonPath("$.items[2].id").value(evidence3.id().toString()))
            .andExpect(jsonPath("$.page").value(String.valueOf(page)))
            .andExpect(jsonPath("$.size").value(String.valueOf(size)))
            .andExpect(jsonPath("$.sort").value(String.valueOf(sort)))
            .andExpect(jsonPath("$.order").value(String.valueOf(order)))
            .andExpect(jsonPath("$.total").value(String.valueOf(evidences.size())));

        then(useCase).should().getEvidenceList(eq(param));
    }
}
