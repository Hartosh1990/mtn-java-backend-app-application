package com.sap.nextgen.vlm.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DataEndpoint {

	GET_COMPANY_SEARCH_RESULTS,
    MTN_DASHBOARD_DATA,
	SAVE_MTN_COMPANY,
	GET_MTN_COMPANY_PROFILE;

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
