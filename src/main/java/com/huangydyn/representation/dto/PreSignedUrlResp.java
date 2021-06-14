package com.huangydyn.representation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PreSignedUrlResp {

    private String preSignedUrl;
}
