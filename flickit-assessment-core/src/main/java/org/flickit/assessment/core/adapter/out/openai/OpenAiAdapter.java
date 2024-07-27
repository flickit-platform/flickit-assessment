package org.flickit.assessment.core.adapter.out.openai;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.core.application.port.out.aireport.CreateAssessmentAttributeAiPort;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

@Component
@AllArgsConstructor
public class OpenAiAdapter implements CreateAssessmentAttributeAiPort {

    private final OpenAiProperties openAiProperties;

    @SneakyThrows
    @Override
    public String createReport(File file) {

        String fileContent = new String(Files.readAllBytes(file.toPath()));

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("model", openAiProperties.getModel());

        JsonObject message = new JsonObject();
        message.addProperty("role", openAiProperties.getRole());
        message.addProperty("content", openAiProperties.getPrompt() + fileContent);

        jsonBody.add("messages", new Gson().toJsonTree(Collections.singletonList(message)));
        jsonBody.addProperty("temperature", 0.7);

        String json = new Gson().toJson(jsonBody);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(openAiProperties.getApiUrl());
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + openAiProperties.getApiKey());

            request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getEntity() != null) {
                    String responseString = new String(response.getEntity().getContent().readAllBytes());

                    JsonObject jsonResponse = new Gson().fromJson(responseString, JsonObject.class);
                    JsonElement contentElement = jsonResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content");
                    return contentElement.getAsString();
                } else {
                    throw new IOException("Response entity is null");
                }
            }
        }
    }
}
