package com.sap.nextgen.vlm.rmo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.ida.eacp.nucleus.data.client.annotation.Dimension;
import com.sap.ida.eacp.nucleus.data.client.annotation.Measure;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MtnDashboardRMO {


    public static final String MTN_ID = "MTN_ID";
    public static final String MTN_VALUE = "MTN_COMPANY";


    @JsonProperty(MTN_ID)
    @Measure(label="MTN ID")
    private Integer mtnId;

    @JsonProperty(MTN_VALUE)
    @Dimension(label="Company Name")
    private String companyName;

}
