package com.sap.nextgen.vlm.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VlmConstants {
    bcIndustry,
    CurrencyData,
    RegionData,
    CountryData,
    fyValue,
    ttmValue,
    childLevel,
    level_info;

    @JsonValue
    @Override
    public String toString() {
    	return name().toLowerCase();
    }
    
}
