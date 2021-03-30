package com.sap.nextgen.vlm.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DataEndpoint {

    CLOUD_TRANSACTIONS_SALES_ADRM,
    CLOUD_TRANSACTIONS_SALES_ADRM_BY_REGION,
    CLOUD_TRANSACTIONS_SALES_ADRM_BY_SALESBAG,
    CLOUD_TRANSACTIONS_SALES_DEALS,
    SOFTWARE_TRANSACTIONS_SALES_ADRM,
    SOFTWARE_TRANSACTIONS_SALES_ADRM_BY_REGION,
    SOFTWARE_TRANSACTIONS_SALES_DEALS,
    SOFTWARE_TRANSACTIONS_SALES_ADRM_BY_SALESBAG,
    STRATEGIC_RENEWALS_ANALYSIS,
    STRATEGIC_RENEWALS_ANALYSIS_TOTALS,
    STRATEGIC_RENEWALS_ANALYSIS_ITEMS,
    STRATEGIC_TOTAL_ESCALATIONS,
    CLOUD_TRANSACTIONS_SALES_ADRM_S4,
    SOFTWARE_TRANSACTIONS_SALES_ADRM_S4,
    STRATEGIC_ESCALATIONS_BY_PROCESS_TYPE,
    STRATEGIC_ESCALATIONS_BY_CUSTOMER,
	GET_COMPANY_SEARCH_RESULTS,
	SAVE_MTN_COMPANY;

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
