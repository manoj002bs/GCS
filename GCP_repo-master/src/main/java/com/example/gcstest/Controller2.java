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

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@RequestMapping("/gcp")
public class Controller2 {

    @Autowired
    private Storage storage;

    @GetMapping("/send-data")
    public String sendData() {
        BlobId id = BlobId.of("gs://dev_gcs_spectr-25069_bucket", "TestGCPFile");
        BlobInfo info = BlobInfo.newBuilder(id).build();
        File file = new File("D:","TestGCSFile.txt");
        byte[] arr = null;
        try {
            arr = Files.readAllBytes(Paths.get(file.toURI()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        storage.create(info,arr);
        return "File Upload success";
    }

}
