package org.flickit.assessment.users.application.port.out.minio;

import java.time.Duration;

public interface CreateFileDownloadLinkPort {

    /**
     * @param filePath path of file in minio and stored in DB
     * @param expiryDuration expiration time of created link
     * @return download link, null if file does not exist
     */
    String createDownloadLink(String filePath, Duration expiryDuration);

    /**
     * @param filePath path of file in minio and stored in DB
     * @param expiryDuration expiration time of created link
     * @return download link, null if file does not exist or an exception occurred
     */
    String createDownloadLinkSafe(String filePath, Duration expiryDuration);
}
