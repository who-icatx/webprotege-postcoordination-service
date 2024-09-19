package edu.stanford.protege.webprotege.postcoordinationservice;

import edu.stanford.protege.webprotege.ipc.WebProtegeIpcApplication;
import edu.stanford.protege.webprotege.postcoordinationservice.config.MinioProperties;
import io.minio.MinioClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({WebProtegeIpcApplication.class})
@EnableCaching
public class WebprotegePostcoordinationServiceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebprotegePostcoordinationServiceServiceApplication.class, args);
    }


    @Bean
    MinioClient minioClient(MinioProperties properties) {
        return MinioClient.builder()
                          .credentials(properties.getAccessKey(), properties.getSecretKey())
                          .endpoint(properties.getEndPoint())
                          .build();
    }
}
