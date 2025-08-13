package com.xiahou.yu.paaswebserver.dynamic;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeCoercing implements Coercing<LocalDateTime, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
        if (dataFetcherResult instanceof LocalDateTime) {
            return ((LocalDateTime) dataFetcherResult).format(FORMATTER);
        } else if (dataFetcherResult instanceof String) {
            return (String) dataFetcherResult;
        } else {
            throw new CoercingSerializeException("Expected a LocalDateTime object.");
        }
    }

    @Override
    public LocalDateTime parseValue(Object input) throws CoercingParseValueException {
        try {
            if (input instanceof String) {
                return LocalDateTime.parse((String) input, FORMATTER);
            } else {
                throw new CoercingParseValueException("Expected a String");
            }
        } catch (DateTimeParseException e) {
            throw new CoercingParseValueException(String.format("Not a valid date: '%s'.", input), e);
        }
    }

    @Override
    public LocalDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
        if (input instanceof StringValue) {
            try {
                return LocalDateTime.parse(((StringValue) input).getValue(), FORMATTER);
            } catch (DateTimeParseException e) {
                throw new CoercingParseLiteralException("Value is not a valid DateTime: " + input, e);
            }
        } else {
            throw new CoercingParseLiteralException("Expected a StringValue.");
        }
    }
}
