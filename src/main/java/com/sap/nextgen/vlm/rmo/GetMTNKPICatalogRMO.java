package com.sap.nextgen.vlm.rmo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.ida.eacp.nucleus.data.client.annotation.Dimension;

import lombok.Data;

@Data
public class GetMTNKPICatalogRMO {

    public static final String KPI_ID = "kpiId";
    public static final String TEXT = "text";
    public static final String IS_SELECTED = "IsSelected";


    @JsonProperty(TEXT)
    @Dimension(label="Text")
    private String text;

    @JsonProperty(KPI_ID)
    @Dimension(label="KPI ID")
    private String kpiId;

    @JsonProperty(IS_SELECTED)
    @Dimension(label="IsSelected")
    private String isSelected;
}
