package org.flickit.assessment.kit.adapter.out.uploaddsl;

import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
        checkZipFileSecurity(dslFile);
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

    private void checkZipFileSecurity(MultipartFile dslFile) {
        try {
            File file = new File(dslFile.getOriginalFilename());
            FileUtils.writeByteArrayToFile(file, dslFile.getBytes());
            ZipFile zipFile = new ZipFile(file);
            FileUtils.forceDelete(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            int THRESHOLD_ENTRIES = 10000;
            int THRESHOLD_SIZE = 1000_000_000; // 1 GB
            double THRESHOLD_RATIO = 10;
            int totalSizeArchive = 0;
            int totalEntryArchive = 0;

            while (entries.hasMoreElements()) {
                ZipEntry ze = entries.nextElement();
                InputStream in = new BufferedInputStream(zipFile.getInputStream(ze));

                totalEntryArchive++;

                int nBytes;
                byte[] buffer = new byte[2048];
                int totalSizeEntry = 0;

                while ((nBytes = in.read(buffer)) > 0) {
                    totalSizeEntry += nBytes;
                    totalSizeArchive += nBytes;

                    double compressionRatio = (double) totalSizeEntry / ze.getCompressedSize();
                    if (compressionRatio > THRESHOLD_RATIO) {
                        // ratio between compressed and uncompressed data is highly suspicious, looks like a Zip Bomb Attack
                        throw new ZipBombException();
                    }
                }

                if (totalSizeArchive > THRESHOLD_SIZE || totalEntryArchive > THRESHOLD_ENTRIES) {
                    // the uncompressed data size is too much for the application resource capacity
                    throw new ZipBombException();
                }

            }
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

    record AssessmentKitRequest(String dslContent) {
    }

}
