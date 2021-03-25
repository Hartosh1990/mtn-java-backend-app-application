package com.sap.nextgen.vlm.rmo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.ida.eacp.nucleus.data.client.annotation.Dimension;
import com.sap.ida.eacp.nucleus.data.client.annotation.Measure;
import com.sap.ida.eacp.nucleus.data.client.model.response.data.DataType;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetMTNSearchResultRMO {


    public static final String CIQ_ID = "ciq_id";
    public static final String VALUE = "value";


    @JsonProperty(VALUE)
    @Dimension(label="Company Name")
    private String companyName;

    @JsonProperty(CIQ_ID)
    @Dimension(label="CIQ ID")
    private String ciq_id;
    
}
