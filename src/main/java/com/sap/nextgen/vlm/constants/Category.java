package com.sap.nextgen.vlm.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    cloud,
    onprem;

    @JsonValue
    @Override
    public String toString() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static DataEndpoint fromString(String value) {
        return DataEndpoint.valueOf(value.toUpperCase());
    }
}
