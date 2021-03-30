package com.sap.nextgen.vlm.rmo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sap.ida.eacp.nucleus.data.client.annotation.Dimension;

import lombok.Data;

@Data
@JsonIgnoreProperties(
value = {"ciq_id", "isTTM", "companyId", "companyName", "country", "currency", "denomination", "employees",
		"fyUpdatedDate", "industry", "isPeerDataAvailable", "isQuesDataAvailable", "mtnName",
		"operInc", "region", "revenue", "updatedDate"}
)

public class SaveMTNCompanyRMO {

	public static final String MTNID = "mtnId";

    @JsonProperty(MTNID)
    @Dimension(label="MTN ID")
    private String mtnId;

}
