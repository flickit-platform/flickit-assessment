package org.flickit.assessment.core.adapter.out.openai;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeAiInsightPort;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.List;

@Component
@AllArgsConstructor
public class OpenAiAdapter implements CreateAttributeAiInsightPort {

    private static final String CHOICES_FIELD = "choices";
    private static final String MESSAGE_FIELD = "message";
    private static final String CONTENT_FIELD = "content";

    private final OpenAiProperties openAiProperties;
    private final RestTemplate openAiRestTemplate;

    @SneakyThrows
    @Override
    public String generateInsight(InputStream inputStream, Attribute attribute) {
        String text = convertExcelToText(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiProperties.getApiKey());

        var content = openAiProperties.createPrompt(attribute.getTitle(), attribute.getDescription()) + text;
        var message = List.of(new OpenAiRequest.Message(openAiProperties.getRole(), content));
        var request = new OpenAiRequest(openAiProperties.getModel(), message, openAiProperties.getTemperature());
        HttpEntity<OpenAiRequest> requestEntity = new HttpEntity<>(request, headers);

        var responseEntity = openAiRestTemplate.exchange(
            openAiProperties.getApiUrl(),
            HttpMethod.POST,
            requestEntity,
            JsonNode.class
        );
        return extractContentFromResponse(responseEntity.getBody());
    }

    private String extractContentFromResponse(JsonNode responseBody) throws IOException {
        if (responseBody != null && responseBody.has(CHOICES_FIELD)) {
            JsonNode choices = responseBody.get(CHOICES_FIELD);
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode messageNode = choices.get(0).get(MESSAGE_FIELD);
                if (messageNode != null && messageNode.has(CONTENT_FIELD)) {
                    return messageNode.get(CONTENT_FIELD).asText();
                } else {
                    throw new IOException("Invalid response format: 'message' or 'content' field is missing");
                }
            }
            throw new IOException("Invalid response format: 'choices' array is empty or not an array");
        } else {
            throw new IOException("Invalid response format: 'choices' field is missing");
        }
    }

    private record OpenAiRequest(String model, List<Message> messages, double temperature) {
        private record Message(String role, String content) {
        }
    }


    public static String convertExcelToText(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        StringBuilder textBuilder = new StringBuilder();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            textBuilder.append("Sheet: ").append(sheet.getSheetName()).append("\n");

            for (Row row : sheet) {
                for (Cell cell : row) {
                    String cellValue = getCellValue(cell);
                    textBuilder.append(cellValue).append("\t");
                }
                textBuilder.append("\n");
            }
            textBuilder.append("\n");
        }

        workbook.close();
        return textBuilder.toString();
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
