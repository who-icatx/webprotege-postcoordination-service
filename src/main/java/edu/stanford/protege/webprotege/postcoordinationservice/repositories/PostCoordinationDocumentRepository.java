package edu.stanford.protege.webprotege.postcoordinationservice.repositories;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class PostCoordinationDocumentRepository {


    private final MinioPostCoordinationDocumentLoader documentLoader;

    private final ObjectMapper objectMapper;

    public PostCoordinationDocumentRepository(MinioPostCoordinationDocumentLoader documentLoader, ObjectMapper objectMapper) {
        this.documentLoader = documentLoader;
        this.objectMapper = objectMapper;
    }

    public Stream<WhoficEntityPostCoordinationSpecification> fetchPostCoordinationSpecifications(String location) {
        return fetchDataStream(location, "whoficEntityPostcoordinationSpecification", WhoficEntityPostCoordinationSpecification.class);
    }

    public Stream<WhoficCustomScalesValues> fetchCustomScalesValues(String location) {
        return fetchDataStream(location, "postcoordinationScaleCustomization", WhoficCustomScalesValues.class);
    }

    private <T> Stream<T> fetchDataStream(String location, String expectedArrayName, Class<T> targetType) {
        try {
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser jsonParser = jsonFactory.createParser(documentLoader.fetchPostCoordinationDocument(location));

            if (jsonParser.nextToken() == JsonToken.START_ARRAY) {
                throw new IllegalStateException("Unexpected array");
            }

            jsonParser.nextToken();

            if (!jsonParser.getCurrentName().equals(expectedArrayName) && jsonParser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Expected the array of " + expectedArrayName);
            }

            jsonParser.nextToken();

            return StreamSupport.stream(
                    new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
                        @Override
                        public boolean tryAdvance(Consumer<? super T> action) {
                            try {
                                if (jsonParser.nextToken() == JsonToken.END_ARRAY) {
                                    return false;
                                }
                                JsonNode node = objectMapper.readTree(jsonParser);
                                T element = objectMapper.treeToValue(node, targetType);
                                action.accept(element);
                                return true;
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        }
                    }, false);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
