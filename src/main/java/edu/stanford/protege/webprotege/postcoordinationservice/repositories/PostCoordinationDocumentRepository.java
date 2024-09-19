package edu.stanford.protege.webprotege.postcoordinationservice.repositories;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public Stream<WhoficEntityPostCoordinationSpecification> fetchFromDocument(String location) {

        try {
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser jsonParser = jsonFactory.createParser(documentLoader.fetchPostCoordinationDocument(location));

            if (jsonParser.nextToken() == JsonToken.START_ARRAY) {
                throw new IllegalStateException("Unexpected array");
            }

            jsonParser.nextToken();

            if (!jsonParser.getCurrentName().equals("whoficEntityPostcoordinationSpecification") && jsonParser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Expected the array of postCoordination specifications");
            }

            jsonParser.nextToken();

            return StreamSupport.stream(
                    new Spliterators.AbstractSpliterator<>(Long.MAX_VALUE, Spliterator.ORDERED) {
                        @Override
                        public boolean tryAdvance(Consumer<? super WhoficEntityPostCoordinationSpecification> action) {
                            try {

                                if (jsonParser.nextToken() == JsonToken.END_ARRAY) {
                                    return false;
                                }

                                JsonNode node = objectMapper.readTree(jsonParser);
                                WhoficEntityPostCoordinationSpecification person = objectMapper.treeToValue(node, WhoficEntityPostCoordinationSpecification.class);
                                action.accept(person);
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
