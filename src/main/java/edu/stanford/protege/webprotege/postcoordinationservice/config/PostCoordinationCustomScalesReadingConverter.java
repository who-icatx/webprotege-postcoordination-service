package edu.stanford.protege.webprotege.postcoordinationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationCustomScalesValueEvent;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class PostCoordinationCustomScalesReadingConverter implements Converter<Document, PostCoordinationCustomScalesValueEvent> {


    private final ObjectMapper objectMapper;

    public PostCoordinationCustomScalesReadingConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public PostCoordinationCustomScalesValueEvent convert(Document source) {
        return objectMapper.convertValue(source, PostCoordinationCustomScalesValueEvent.class);
    }
}
