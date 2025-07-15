package edu.stanford.protege.webprotege.postcoordinationservice.repositories;

import edu.stanford.protege.webprotege.postcoordinationservice.StorageException;
import edu.stanford.protege.webprotege.postcoordinationservice.config.MinioProperties;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2024-05-03
 */
@Component
public class MinioPostCoordinationDocumentLoader {

    private final static Logger LOGGER = LoggerFactory.getLogger(MinioPostCoordinationDocumentLoader.class);

    private final MinioClient minioClient;

    private final MinioProperties minioProperties;

    public MinioPostCoordinationDocumentLoader(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    public InputStream fetchPostCoordinationDocument(@Nonnull String location) throws StorageException {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(location)
                    .build());
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new StorageException("Problem reading linearization document object from storage", e);
        } catch (Exception e) {
            LOGGER.error("Error on fetching postcoordination document " , e);
            throw new RuntimeException(e);
        }
    }
}
