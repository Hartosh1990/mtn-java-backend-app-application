package com.sap.nextgen.vlm.rmo;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.ida.eacp.nucleus.data.client.annotation.Dimension;
import com.sap.nextgen.vlm.constants.VlmConstants;
import com.sap.nextgen.vlm.utils.CacheManager;

import lombok.Data;

@Data
@JsonIgnoreProperties(
value = {"ciq_id", "isTTM", "companyId", "companyName", "country", "denomination", "employees",
		"fyUpdatedDate", "industry", "isPeerDataAvailable", "isQuesDataAvailable", "mtnName",
		"operInc", "region", "revenue", "updatedDate"}
)

public class SaveMTNCompanyRMO {

	public static final String MTNID = "mtnId";
	public static final String CURRENCY = "currency";

    @JsonProperty(MTNID)
    @Dimension(label="MTN ID")
    private String mtnId;
    
    @JsonProperty("mtnCurrencyId")
    @Dimension(label="MTN Currency Id",isVisible = false)
    private String mtnCurrencyId;
    
    @JsonProperty(CURRENCY)
    public void unpackCurrencyValue(Map<String,Integer> currencyMap) throws ClientProtocolException, ExecutionException, IOException {
    	this.mtnCurrencyId = currencyMap.get("id").toString();
    }

}
