package org.flickit.assessment.kit.adapter.out.uploaddsl;

import lombok.AllArgsConstructor;
import org.flickit.assessment.kit.adapter.out.uploaddsl.exception.DSLHasSyntaxErrorException;
import org.flickit.assessment.kit.adapter.out.uploaddsl.exception.ZipBombException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.assessmentkit.GetDslContentPort;
import org.flickit.assessment.kit.config.DslParserRestProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
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
    public AssessmentKitDslModel getDslContent(MultipartFile dslFile) {
        String dslContent = uniteDslFiles(dslFile);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AssessmentKitRequest> requestEntity = new HttpEntity<>(new AssessmentKitRequest(dslContent), headers);
        try {
            var responseEntity = dslParserRestTemplate.exchange(
                    properties.getUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    AssessmentKitDslModel.class
            );
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            throw new DSLHasSyntaxErrorException(e.getMessage());
        }
    }

    private String uniteDslFiles(MultipartFile dslFile) {
        try (InputStream dslFileStream = dslFile.getInputStream();
             ZipInputStream zipInputStream = new ZipInputStream(dslFileStream)) {
            StringBuilder allContent = new StringBuilder();
            ZipEntry entry = checkZipBombAttack(zipInputStream);
            while (entry != null) {
                long decompressedSize = entry.getSize();
                if (decompressedSize > 100_000_000) {
                    throw new ZipBombException();
                }

                String name = entry.getName();
                if (name.endsWith(".ak")) {
                    String fileBaseName = name.substring(name.lastIndexOf('/') + 1);
                    String content = StreamUtils.copyToString(zipInputStream, StandardCharsets.UTF_8);
                    String trimContent = trimContent(content);
                    allContent.append("\n")
                            .append("// BEGIN FILE ").append(fileBaseName)
                            .append(trimContent);
                }

                entry = checkZipBombAttack(zipInputStream);
            }
            zipInputStream.closeEntry();
            return allContent.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ZipEntry checkZipBombAttack(ZipInputStream zipInputStream) throws IOException {
        ZipEntry entry;
        if ((entry = zipInputStream.getNextEntry()) != null && entry.getSize() > 100_000_000) {
            throw new ZipBombException();
        }
        return entry;
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

    record AssessmentKitRequest(String dslContent) {
    }

}
