package com.huangydyn.representation;

import com.huangydyn.infustructure.FileClient;
import com.huangydyn.representation.dto.PreSignedUrlResp;
import com.huangydyn.representation.dto.UploadFileResponse;
import com.huangydyn.utils.FileUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@RestController
@RequestMapping("/v1")
public class FileController {

    private final FileClient fileClient;

    public FileController(FileClient fileClient) {
        this.fileClient = fileClient;
    }

    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Upload Single file to AWS S3 Bucket")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Upload single file successfully"),
            @ApiResponse(code = 400, message = "Upload single file failed")
    })
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = fileClient.uploadFile(file.getBytes());
        return UploadFileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .fileName(fileName)
                .build();
    }

    @GetMapping("/files")
    @ApiOperation("Download Single file from AWS S3 Bucket")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String fileName) {
        InputStream inputStream = fileClient.downloadFile(fileName);
        return ResponseEntity.ok()
                .contentType(FileUtils.getFileContentType(fileName))
                .body(new InputStreamResource(inputStream));
    }

    @GetMapping("/files/pre-signed-url")
    @ApiOperation("GetPeSignedUrl")
    public PreSignedUrlResp getPreSignedUrl(@RequestParam String fileName) {
        String preSignedUrl = fileClient.getPreSignedUrl(fileName);
        return PreSignedUrlResp.builder().preSignedUrl(preSignedUrl).build();
    }
}