package edu.stanford.protege.webprotege.postcoordinationservice;

import org.junit.jupiter.api.extension.*;
import org.slf4j.*;
import org.testcontainers.containers.*;
import org.testcontainers.utility.DockerImageName;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-06-06
 */
public class IntegrationTest implements BeforeAllCallback, AfterAllCallback {

    private final static Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    private MongoDBContainer mongoDBContainer;
    private RabbitMQContainer rabbitContainer;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        setUpMongo();
        setUpRabbitMq();
    }

    private void setUpMongo() {
        var imageName = DockerImageName.parse("mongo");
        mongoDBContainer = new MongoDBContainer(imageName)
                .withExposedPorts(27017);
        mongoDBContainer.start();

        var mappedHttpPort = mongoDBContainer.getMappedPort(27017);
        logger.info("MongoDB port 27017 is mapped to {}", mappedHttpPort);
        System.setProperty("spring.data.mongodb.port", Integer.toString(mappedHttpPort));
    }

    private void setUpRabbitMq() {
        var imageName = DockerImageName.parse("rabbitmq:3.7.25-management-alpine");
        rabbitContainer = new RabbitMQContainer(imageName)
                .withExposedPorts(5672);
        rabbitContainer.start();

        System.setProperty("spring.rabbitmq.host", rabbitContainer.getHost());
        System.setProperty("spring.rabbitmq.port", String.valueOf(rabbitContainer.getAmqpPort()));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        if (mongoDBContainer != null) {
            mongoDBContainer.close();
        }
        if (rabbitContainer != null) {
            rabbitContainer.close();
        }
    }

}
