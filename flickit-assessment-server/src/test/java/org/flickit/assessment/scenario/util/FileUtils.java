package org.flickit.assessment.scenario.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@UtilityClass
public class FileUtils {

    @SneakyThrows
    public static MockMultipartFile createMultipartFile(String fileName, String name, String contentType) {
        try (InputStream is = FileUtils.class.getResourceAsStream("/" + fileName)) {
            assertNotNull(is);
            return new MockMultipartFile(name, fileName, contentType, is);
        }
    }

    @SneakyThrows
    public static String readFileToString(String fileName) {
        try (InputStream is = FileUtils.class.getResourceAsStream("/" + fileName)) {
            assertNotNull(is);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
