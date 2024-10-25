package edu.stanford.protege.webprotege.postcoordinationservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith({SpringExtension.class, IntegrationTest.class})
@ActiveProfiles("test")
public class WebprotegePostCoordinationServiceApplicationTest {

    @Test
    public void contextLoads() {
    }

}
