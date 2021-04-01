package com.sap.nextgen.vlm.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VlmConstants {
    bcIndustry;

    @JsonValue
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
}
