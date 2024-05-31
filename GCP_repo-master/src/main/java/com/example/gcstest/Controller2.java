package com.example.gcstest;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/gcp")
public class Controller2 {

    @Autowired
    private Storage storage;

    @GetMapping("/send-data")
    public String sendData() {
        String bucketName = "dev_gcs_spectr-25069_bucket";
        String objectName = "TestGCPFile";
        BlobId id = BlobId.of(bucketName, objectName);
        BlobInfo info = BlobInfo.newBuilder(id).build();

        File file = new File("D:", "TestGCSFile.txt");
        byte[] arr;
        try {
            arr = Files.readAllBytes(Paths.get(file.toURI()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }

        storage.create(info, arr);
        return "File Upload success";
    }
}

