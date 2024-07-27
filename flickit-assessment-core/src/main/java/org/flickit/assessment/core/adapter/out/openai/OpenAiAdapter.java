package org.flickit.assessment.core.adapter.out.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.core.application.port.out.aireport.CreateAssessmentAttributeAiPort;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;


@Component
@AllArgsConstructor
public class OpenAiAdapter implements CreateAssessmentAttributeAiPort {

    private final OpenAiProperties openAiProperties;

    @SneakyThrows
    @Override
    public String createReport(InputStream inputStream) {

        String fileContent = readInputStream(inputStream);

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("model", openAiProperties.getModel());

        JsonObject message = new JsonObject();
        message.addProperty("role", openAiProperties.getRole());
        message.addProperty("content", openAiProperties.getPrompt() + fileContent);

        jsonBody.add("messages", new Gson().toJsonTree(Collections.singletonList(message)));
        jsonBody.addProperty("temperature", 0.7);

        String json = new Gson().toJson(jsonBody);

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(openAiProperties.getApiUrl()))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + openAiProperties.getApiKey())
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.body() != null) {
            String responseString = response.body();

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

    private String readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8);
    }
}
