package com.huangydyn.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static final String NO_EXTENSION = "";

    private static final int CHAR_NOT_FOUND = -1;

    public static String getFileExtension(String fileName) {
        if (StringUtils.isEmpty(fileName) || fileName.lastIndexOf('.') == CHAR_NOT_FOUND) {
            return NO_EXTENSION;
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public static String getFileExtension(byte[] data) throws MimeTypeException {
        String contentType = new Tika().detect(data);
        return MimeTypes.getDefaultMimeTypes().forName(contentType).getExtension();
    }

    public static MediaType getFileContentType(String fileName) {
        String mediaType = new Tika().detect(fileName);
        if (StringUtils.isEmpty(mediaType)) {
            log.error("fileType type:{} not found", fileName);
            return MediaType.APPLICATION_OCTET_STREAM;
        } else {
            return MediaType.valueOf(mediaType);
        }
    }
}

