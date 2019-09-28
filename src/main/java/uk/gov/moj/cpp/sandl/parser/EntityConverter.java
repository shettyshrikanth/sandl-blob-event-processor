package uk.gov.moj.cpp.sandl.parser;

import uk.gov.moj.cpp.sandl.entity.Assignment;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class EntityConverter {

    final ObjectMapper mapper = new ObjectMapper();
    {
        mapper.registerModule(new JavaTimeModule());
    }


    public Assignment convert(final Map<String, Object> properties) {
        try {
            return mapper.convertValue(properties, Assignment.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
