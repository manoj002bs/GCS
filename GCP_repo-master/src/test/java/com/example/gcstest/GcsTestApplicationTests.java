package com.example.gcstest;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.awaitility.Awaitility;
import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfSystemProperty(named = "it.storage", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = {GcsTestApplication.class})
class GcsTestApplicationTests {

	private final String filename = String.format("file-%s.txt", UUID.randomUUID());
	@Autowired
	private Storage storage;
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Value("${gcs-resource-test-bucket}")
	private String bucketName;
	@LocalServerPort
	private int port;
	private String appUrl;

	@BeforeEach
	void initializeAppUrl() {
		this.appUrl = "http://localhost:" + this.port;
	}

	@AfterClass
	void cleanupCloudStorage() {
		BlobId blobId = BlobId.of(this.bucketName, filename);
		Blob blob = storage.get(blobId);
		if (blob != null) {
			blob.delete();
		}
	}

	@Test
	void testGcsResourceIsLoaded() {
		BlobId blobId = BlobId.of(this.bucketName, filename);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
		this.storage.create(blobInfo, "Good Morning!".getBytes(StandardCharsets.UTF_8));

		// Verify the contents of the uploaded file.
		String getUrl =
				UriComponentsBuilder.fromHttpUrl(this.appUrl + "/")
						.queryParam("filename", filename)
						.toUriString();
		Awaitility.await()
				.atMost(15, TimeUnit.SECONDS)
				.untilAsserted(
						() -> {
							String result = this.testRestTemplate.getForObject(getUrl, String.class);
							assertThat(result).isEqualTo("Good Morning!\n");
						});

	}

}
