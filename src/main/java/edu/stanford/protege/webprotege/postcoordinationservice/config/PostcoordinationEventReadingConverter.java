package edu.stanford.protege.webprotege.postcoordinationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationEvent;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class PostcoordinationEventReadingConverter implements Converter<Document, PostCoordinationEvent> {


    private final ObjectMapper objectMapper;

    public PostcoordinationEventReadingConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public PostCoordinationEvent convert(Document source) {
        return objectMapper.convertValue(source, PostCoordinationEvent.class);
    }
}