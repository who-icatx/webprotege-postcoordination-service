package edu.stanford.protege.webprotege.postcoordinationservice;

import edu.stanford.protege.webprotege.ipc.WebProtegeIpcApplication;
import edu.stanford.protege.webprotege.postcoordinationservice.config.MinioProperties;
import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Import({WebProtegeIpcApplication.class})
@EnableCaching
public class WebprotegePostcoordinationServiceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebprotegePostcoordinationServiceServiceApplication.class, args);
    }


    @Bean
    MinioClient minioClient(MinioProperties properties) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)   // connection timeout
                .readTimeout(600, TimeUnit.SECONDS)      // read timeout for large files
                .writeTimeout(600, TimeUnit.SECONDS)     // write timeout
                .retryOnConnectionFailure(true)         // retry on connection failure
                .build();

        return MinioClient.builder()
                          .credentials(properties.getAccessKey(), properties.getSecretKey())
                          .endpoint(properties.getEndPoint())
                          .httpClient(httpClient)
                          .build();
    }
}
