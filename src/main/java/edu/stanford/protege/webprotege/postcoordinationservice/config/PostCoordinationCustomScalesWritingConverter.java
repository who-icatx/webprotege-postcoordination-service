package edu.stanford.protege.webprotege.postcoordinationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationCustomScalesValueEvent;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

@WritingConverter
public class PostCoordinationCustomScalesWritingConverter implements Converter<PostCoordinationCustomScalesValueEvent, Document> {
    private final ObjectMapper objectMapper;

    public PostCoordinationCustomScalesWritingConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public Document convert(@NonNull PostCoordinationCustomScalesValueEvent source) {
        return objectMapper.convertValue(source, Document.class);
    }
}
