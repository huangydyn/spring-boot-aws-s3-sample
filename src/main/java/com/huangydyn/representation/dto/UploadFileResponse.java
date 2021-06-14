package com.huangydyn.representation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadFileResponse {

    private String originalFileName;

    private String fileName;
}
