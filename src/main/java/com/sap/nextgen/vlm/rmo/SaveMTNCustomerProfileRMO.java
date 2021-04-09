package com.sap.nextgen.vlm.rmo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.ida.eacp.nucleus.data.client.annotation.Dimension;

import lombok.Data;

@Data
@JsonIgnoreProperties(
value = {"results"}
)

public class SaveMTNCustomerProfileRMO {
	public static final String SUCCESS = "success";

    @JsonProperty(SUCCESS)
    @Dimension(label="success")
    private boolean success;

}
