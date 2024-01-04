package org.flickit.assessment.kit.adapter.out.uploaddsl;

import lombok.AllArgsConstructor;
import org.flickit.assessment.kit.application.port.out.assessmentkit.GetDslContentPort;
import org.flickit.assessment.kit.config.DslParserRestProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
@AllArgsConstructor
public class DslParserAdapter implements GetDslContentPort {

    private final RestTemplate dslParserRestTemplate;
    private final DslParserRestProperties properties;

    @Override
    public String getDslContent(MultipartFile dslFile) {
        String dslContent = uniteDslFiles(dslFile);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(dslContent, headers);
        var responseEntity = dslParserRestTemplate.exchange(
            properties.getUrl(),
            HttpMethod.POST,
            requestEntity,
            new ParameterizedTypeReference<String>() {
            }
        );

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {

        }

        if (responseEntity.getBody() != null) {

        }

        return responseEntity.getBody();
    }

    private String uniteDslFiles(MultipartFile dslFile) {
        try (InputStream dslFileStream = dslFile.getInputStream();
             ZipInputStream zipInputStream = new ZipInputStream(dslFileStream)) {
            StringBuilder allContent = new StringBuilder();
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".ak")) {
                    String fileBaseName = name.substring(name.lastIndexOf('/') + 1);
                    String content = StreamUtils.copyToString(zipInputStream, StandardCharsets.UTF_8);
                    String trimContent = trimContent(content);
                    allContent.append("\n")
                        .append("// BEGIN FILE ").append(fileBaseName)
                        .append(trimContent);
                }
            }
            zipInputStream.closeEntry();
            return allContent.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String trimContent(String content) {
        StringBuilder newContent = new StringBuilder();
        for (String line : content.split("\\r?\\n")) {
            if (!line.trim().startsWith("import")) {
                newContent.append('\n').append(line);
            } else {
                newContent.append("\n//").append(line);
            }
        }
        return newContent.toString();
    }

}
