package com.uploadcsv.csvupload.controller;

import com.uploadcsv.csvupload.response.ResponseMessage;
import com.uploadcsv.csvupload.service.Service;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/files")
public class Controller {
    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }
    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage>uploadFile(@RequestParam("file")MultipartFile file){
        if(service.haCsvFormat(file)){
            service.processAndSaveData(file);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseMessage("upload file successFully : " + file.getOriginalFilename()));
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("please upload file"));
    }

    @PostMapping("/download")
    public ResponseEntity<Resource> downloadFile(){
        String fileName = "userData.csv";
        ByteArrayInputStream fileData = service.load();
        InputStreamResource resource = new InputStreamResource(fileData);
        return  ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/csv")).body(resource);
    }
}
