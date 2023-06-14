package org.flickit.flickitassessmentcore.adapter.in.rest;

import org.flickit.flickitassessmentcore.adapter.in.rest.qualityattribute.CalculateQAMaturityLevelResponseDto;
import org.flickit.flickitassessmentcore.adapter.in.rest.qualityattribute.CalculateQualityAttributeMaturityLevelRestController;
import org.flickit.flickitassessmentcore.application.port.in.qualityattribute.CalculateQAMaturityLevelCommand;
import org.flickit.flickitassessmentcore.application.service.CalculateQAMaturityLevelServiceContext;
import org.flickit.flickitassessmentcore.application.service.qualityattribute.CalculateQualityAttributeMaturityLevelService;
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
@WebMvcTest(CalculateQualityAttributeMaturityLevelRestController.class)
public class CalculateQualityAttributeMaturityLevelRestControllerIntegrationTest {
    private final CalculateQAMaturityLevelServiceContext context = new CalculateQAMaturityLevelServiceContext();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CalculateQualityAttributeMaturityLevelService service;
    @Autowired
    private JacksonTester<CalculateQAMaturityLevelResponseDto> jacksonTester;

    @Test
    @Disabled
    public void greetingShouldReturnDefaultMessage() throws Exception {
        CalculateQAMaturityLevelCommand command = new CalculateQAMaturityLevelCommand(
            context.getQualityAttribute().getId(),
            context.getResult().getId()
        );
        when(service.calculateQualityAttributeMaturityLevel(command)).thenReturn(context.getMaturityLevel2());
        Long kitId = context.getKit().getId();
        Long subId = context.getSubject().getId();
        Long qaId = context.getQualityAttribute().getId();
        String url = "/" + kitId + "/" + subId + "/" + qaId + "/maturity-level";
        MockHttpServletResponse response = this.mockMvc.perform(post(url)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(
            new CalculateQAMaturityLevelResponseDto(context.getMaturityLevel2())
        ).getJson());
    }

}
