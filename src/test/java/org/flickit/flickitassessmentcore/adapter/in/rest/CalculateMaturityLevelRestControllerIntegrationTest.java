package org.flickit.flickitassessmentcore.adapter.in.rest;

import org.flickit.flickitassessmentcore.adapter.in.rest.assessmentresult.CalculateMaturityLevelResponseDto;
import org.flickit.flickitassessmentcore.adapter.in.rest.assessmentresult.CalculateMaturityLevelRestController;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateMaturityLevelCommand;
import org.flickit.flickitassessmentcore.application.service.assessmentresult.CalculateMaturityLevelServiceContext;
import org.flickit.flickitassessmentcore.application.service.assessmentresult.CalculateMaturityLevelService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureJsonTesters
@WebMvcTest(CalculateMaturityLevelRestController.class)
public class CalculateMaturityLevelRestControllerIntegrationTest {
    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CalculateMaturityLevelService service;
    @Autowired
    private JacksonTester<CalculateMaturityLevelResponseDto> jacksonTester;

    @Test
    @Disabled
    public void greetingShouldReturnDefaultMessage() throws Exception {
        CalculateMaturityLevelCommand command = new CalculateMaturityLevelCommand(context.getAssessment().getId());
//        when(service.calculateQualityAttributeMaturityLevel(command)).thenReturn(context.getMaturityLevel2());
        Long kitId = context.getKit().getId();
        Long subId = context.getSubject().getId();
        Long qaId = context.getQualityAttribute().getId();
        String url = "/" + kitId + "/" + subId + "/" + qaId + "/maturity-level";
        MockHttpServletResponse response = this.mockMvc.perform(post(url)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(
            new CalculateMaturityLevelResponseDto(context.getResult())
        ).getJson());
    }

}
