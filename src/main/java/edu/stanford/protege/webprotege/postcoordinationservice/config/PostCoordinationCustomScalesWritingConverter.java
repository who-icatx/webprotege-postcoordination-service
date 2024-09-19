package edu.stanford.protege.webprotege.postcoordinationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationCustomScalesValueEvent;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationSpecificationEvent;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class PostCoordinationCustomScalesWritingConverter  implements Converter<PostCoordinationCustomScalesValueEvent, Document> {
    private final ObjectMapper objectMapper;

    public PostCoordinationCustomScalesWritingConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public Document convert(@NotNull PostCoordinationCustomScalesValueEvent source) {
        return objectMapper.convertValue(source, Document.class);
    }
}
