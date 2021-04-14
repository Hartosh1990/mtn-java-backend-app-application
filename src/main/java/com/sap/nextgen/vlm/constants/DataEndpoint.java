package com.sap.nextgen.vlm.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DataEndpoint {

	GET_COMPANY_SEARCH_RESULTS,
    MTN_DASHBOARD_DATA,
	SAVE_MTN_COMPANY,
	GET_MTN_COMPANY_PROFILE,
	GET_MTN_PEER_PROFILE,
	GET_MTN_KPI_METRICS,
	SAVE_MTN_COMPANY_PROFILE_INFO,
	GET_MTN_TREND_ANALYSIS_YEARS,
	GET_MTN_KPI_CATALOG;

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
