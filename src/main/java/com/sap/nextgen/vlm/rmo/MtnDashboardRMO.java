package com.sap.nextgen.vlm.rmo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.ida.eacp.nucleus.data.client.annotation.Dimension;
import com.sap.ida.eacp.nucleus.data.client.annotation.Measure;
import lombok.Data;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "mtn_id",
    "T0401_AutoID",
    "mtn_name",
    "modifiedDate",
    "createdDate",
    "ciq_uid",
    "comp_name",
    "ind"
})

@Data
@Accessors(chain = true)
public class MtnDashboardRMO {

	
    public static final String MTN_ID = "MTN_ID";
    public static final String MTN_VALUE = "MTN_COMPANY";


  //  @JsonProperty(MTN_ID)
  //  @Measure(label="MTN ID")
  //  private Integer mtnId;

   // @JsonProperty(MTN_VALUE)
   // @Dimension(label="Company Name")
   // private String companyName;
    
    @JsonProperty("mtn_id")
    @Measure(label="MTN ID")
    private Integer mtnId;
    
    @JsonProperty("T0401_AutoID")
    @Measure(label="T0401_AutoID")
    private Integer t0401AutoID;
    
    @JsonProperty("mtn_name")
    @Measure(label="mtn_name")
    private String mtnName;
    
    @JsonProperty("modifiedDate")
    @Measure(label="modifiedDate")
    private String modifiedDate;
    
    @JsonProperty("createdDate")
    @Measure(label="createdDate")
    private String createdDate;
    
    @JsonProperty("ciq_uid")
    @Measure(label="ciq_uid")
    private String ciqUid;
    
    @JsonProperty("comp_name")
    @Measure(label="comp_name")
    private String compName;
    
    @JsonProperty("ind")
    @Measure(label="ind")
    private String ind;
    


}
