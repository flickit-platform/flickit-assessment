package org.flickit.assessment.common.config;

import co.novu.api.workflowgroups.request.WorkflowGroupRequest;
import co.novu.api.workflowgroups.responses.WorkflowGroupResponse;
import co.novu.api.workflowgroups.responses.WorkflowGroupResponseData;
import co.novu.api.workflows.requests.WorkflowRequest;
import co.novu.api.workflows.responses.WorkflowResponse;
import co.novu.common.base.Novu;
import co.novu.common.rest.NovuNetworkException;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.NotificationType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnProperty(
    name = "app.novu.init-workflows",
    havingValue = "true",
    matchIfMissing = true)
public class NovuWorkflowInitializer {

    @SneakyThrows
    public NovuWorkflowInitializer(Novu novu, NovuClientProperties properties) {
        String flickitWorkflowGroupId = createWorkflowGroup(novu, properties.getWorkflowGroupName());
        createWorkflows(novu, flickitWorkflowGroupId);
    }

    void createWorkflows(Novu novu, String groupId) throws IOException, NovuNetworkException {
        List<String> workflowNames = novu.getWorkflows(null, null).getData().stream()
            .filter(wf -> Objects.equals(wf.getNotificationGroup().getId(), groupId))
            .map(WorkflowResponse::getName)
            .toList();

        List<String> workflowCodes = Arrays.stream(NotificationType.values())
            .map(NotificationType::getCode)
            .collect(Collectors.toList());
        workflowCodes.removeAll(workflowNames);

        for (String code : workflowCodes) {
            WorkflowRequest request = new WorkflowRequest();
            request.setActive(true);
            request.setName(code);
            request.setSteps(new ArrayList<>());
            request.setNotificationGroupId(groupId);
            novu.createWorkflow(request);
        }
    }

    private String createWorkflowGroup(Novu novu, String groupName) throws IOException, NovuNetworkException {
        Optional<WorkflowGroupResponseData> workflowGroup = novu.getWorkflowGroups()
            .getData().stream()
            .filter(w -> Objects.equals(w.getName(), groupName))
            .findFirst();

        if (workflowGroup.isEmpty()) {
            WorkflowGroupRequest workflowGroupRequest = new WorkflowGroupRequest();
            workflowGroupRequest.setName(groupName);
            WorkflowGroupResponse response = novu.createWorkflowGroup(workflowGroupRequest);
            return response.getData().getId();
        }

        return workflowGroup.get().getId();
    }
}
