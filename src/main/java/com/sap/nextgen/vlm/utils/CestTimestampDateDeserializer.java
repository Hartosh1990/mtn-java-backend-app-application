package com.sap.nextgen.vlm.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.TimeZone;

public class CestTimestampDateDeserializer extends StdDeserializer<LocalDate> {
    private final DateTimeFormatter JS_DATE_FORMAT = DateTimeFormat.forPattern("EEE MMM d yyyy HH:mm:ss 'GMT'Z");
    private final java.time.format.DateTimeFormatter SAP_DATE = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");

    public CestTimestampDateDeserializer() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final String value = p.getValueAsString();

        if (Strings.isNullOrEmpty(value)) return null;
        if ("0000-00-00".equals(value)) return null;

        // FIXME: Workaround as remote query API has date flaw
        // "2015-07-29T22:00:00.000Z" vs. "Thu Sep 15 1994 00:00:00 GMT+0200 (CEST)"
        if(value.matches("\\d+")){
            return LocalDate.parse(value, SAP_DATE);
        }
        else if (value.matches("\\d.+")) {
            final DateTime parse = DateTime.parse(value);
            return cestTimestampToUtcDate(parse);
        } else {
            final String cleanedDate = value.replaceFirst(" \\(\\w+\\)$", "");
            final DateTime parse = JS_DATE_FORMAT.parseDateTime(cleanedDate);

            //Prevent invalid dates like "Fri Dec 31 -0001 00:00:00 GMT+0100 (LMT)"
            if (parse.getYear() <= 0) return null;

            return cestTimestampToUtcDate(parse);
        }
    }

    private LocalDate cestTimestampToUtcDate(DateTime date) {
        if (date == null) return null;

        final DateTime dateTime = date.toDateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Berlin")));

        return LocalDate.of(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
    }
}
