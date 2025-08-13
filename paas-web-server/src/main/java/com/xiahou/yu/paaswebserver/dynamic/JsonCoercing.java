package com.xiahou.yu.paaswebserver.dynamic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

public class JsonCoercing implements Coercing<Object, Object> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object serialize(Object dataFetcherResult) throws CoercingSerializeException {
        if (dataFetcherResult instanceof String) {
            try {
                return objectMapper.readValue((String) dataFetcherResult, Object.class);
            } catch (JsonProcessingException e) {
                return dataFetcherResult;
            }
        }
        return dataFetcherResult;
    }

    @Override
    public Object parseValue(Object input) throws CoercingParseValueException {
        return input;
    }

    @Override
    public Object parseLiteral(Object input) throws CoercingParseLiteralException {
        if (input instanceof StringValue) {
            try {
                return objectMapper.readValue(((StringValue) input).getValue(), Object.class);
            } catch (JsonProcessingException e) {
                throw new CoercingParseLiteralException("Invalid JSON: " + input, e);
            }
        }
        return input;
    }
}
