package edu.stanford.protege.webprotege.postcoordinationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostcoordinationEvent;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;


@WritingConverter
public class PostcoordinationEventWritingConverter implements Converter<PostcoordinationEvent, Document> {
    private final ObjectMapper objectMapper;

    public PostcoordinationEventWritingConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public Document convert(PostcoordinationEvent source) {
        return objectMapper.convertValue(source, Document.class);
    }
}
