package com.uploadcsv.csvupload.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

public interface Service {
    boolean haCsvFormat(MultipartFile file);

    void processAndSaveData(MultipartFile file);

    ByteArrayInputStream load();
}
