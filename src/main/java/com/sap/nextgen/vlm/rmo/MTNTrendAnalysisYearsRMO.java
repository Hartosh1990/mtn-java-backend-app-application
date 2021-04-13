package com.sap.nextgen.vlm.rmo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.ida.eacp.nucleus.data.client.annotation.Dimension;
import com.sap.ida.eacp.nucleus.data.client.annotation.Measure;
import com.sap.ida.eacp.nucleus.data.client.model.response.data.DataType;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MTNTrendAnalysisYearsRMO {


    public static final String YEAR = "Years";
    


    @JsonProperty(YEAR)
    @Dimension(label="Year")
    private Integer Year;

    
    
}
