package edu.stanford.protege.webprotege.postcoordinationservice;

import edu.stanford.protege.webprotege.common.BlobLocation;
import edu.stanford.protege.webprotege.postcoordinationservice.config.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2024-05-03
 */
@Component
public class MinioRevisionHistoryDocumentStorer {

    private final MinioClient minioClient;

    private final MinioProperties minioProperties;

    public MinioRevisionHistoryDocumentStorer(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    public BlobLocation storeDocument(Path documentPath) {
        try {
            var location = generateBlobLocation();
            // Create bucket if necessary
            createBucketIfNecessary(location);
            minioClient.uploadObject(UploadObjectArgs.builder()
                                                     .filename(documentPath.toString())
                                                     .bucket(location.bucket())
                                                     .object(location.name())
                                                     .contentType("application/octet-stream")
                                                     .build());
            return location;
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new StorageException("Problem writing revision history document to storage", e);
        }
    }

    private void createBucketIfNecessary(BlobLocation location) throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(location.bucket()).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(location.bucket()).build());
        }
    }

    private BlobLocation generateBlobLocation() {
        return new BlobLocation(minioProperties.getRevisionHistoryDocumentsBucketName(), generateObjectName());
    }

    private static String generateObjectName() {
        return "revision-history-" + UUID.randomUUID() + ".bin";
    }

}
