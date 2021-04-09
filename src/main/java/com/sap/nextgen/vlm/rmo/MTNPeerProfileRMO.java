package com.sap.nextgen.vlm.rmo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.sap.ida.eacp.nucleus.data.client.annotation.Dimension;
import com.sap.ida.eacp.nucleus.data.client.annotation.Measure;
import com.sap.nextgen.vlm.constants.VlmConstants;
import com.sap.nextgen.vlm.utils.CacheManager;
import com.sap.nextgen.vlm.utils.MasterPackageKey;

import lombok.Data;


@Data
@JsonIgnoreProperties(
value = {"isTTM", "companyId","region", "industry", "currency"//,"updatedDate","operInc","employees", "revenue", "industry",
		//"fyUpdatedDate", "denomination",  "country"
		}
)

@JsonPropertyOrder({
    "companyName",
    "revenue",
    "revenuePerc",
    "operatingInc",
    "operatingIncPerc",
    "employees",
    "country"
})

public class MTNPeerProfileRMO {
	
	public static final String COMPANYNAME = "companyName";	
	public static final String OPERATING_INCOME = "operInc";
	public static final String REVENUE = "revenue";
	public static final String EMPLOYEES = "employees";
	public static final String ISTTM = "isTTM";	
	public static final String COUNTRY = "country";
	//public static final String OPERATING_INCOME_PERC = "operIncPerc";
	//public static final String REVENUE_PERC = "revenuePerc";
	
	private SimpleDateFormat inputDateFormatter = new SimpleDateFormat("yyyy-mm-dd");
	private SimpleDateFormat outputDateFormatter = new SimpleDateFormat("mm/dd/yyyy");
	private short isTTM = 0;
	
    @JsonProperty(COMPANYNAME)
    @Dimension(label="Company Name")
    private String companyName;
    
    @JsonProperty("revenueValue")
    @Measure(label="Revenue")
    private Float revenue;
    

    @JsonProperty("revenuePercValue")
    @Measure(label="Revenue Percentage")
    private Double revenuePerc;
    
    @JsonProperty("opValue")
    @Measure(label="Operating Income")
    private Float operatingInc;
    
    @JsonProperty("opPercValue")
    @Measure(label="Operating Income Percentage")
    private Double operatingIncPerc;
    
   
    
    @JsonProperty("employeesValue")
    @Measure(label="Employees")
    private long employees;
   

    @JsonProperty("countryValue")
    @Dimension(label="Country")
    private String country;
    
    
    @JsonProperty(ISTTM)
    public void getIsTTMFlag(short isTTM) {
    	this.isTTM = isTTM;
    }
    
    @JsonProperty(REVENUE)
    public void unpackRevenueValue(Map<String,JsonNode> revenue) {
    	if(isTTM == 0) {
    		this.revenue = Float.parseFloat(revenue.get(VlmConstants.fyValue.name()).get("value").asText());
    		this.revenuePerc = Double.parseDouble(revenue.get(VlmConstants.fyValue.name()).get("percentage").asText());
    	}else {
    		this.revenue = Float.parseFloat(revenue.get(VlmConstants.ttmValue.name()).get("value").asText());
    		this.revenuePerc = Double.parseDouble(revenue.get(VlmConstants.ttmValue.name()).get("percentage").asText());
    	}
    }
    
    @JsonProperty(OPERATING_INCOME)
    public void unpackOpValue(Map<String,JsonNode> opi) {
    	if(isTTM == 0) {
    		this.operatingInc = Float.parseFloat(opi.get(VlmConstants.fyValue.name()).get("value").asText());
    		this.operatingIncPerc = Double.parseDouble(opi.get(VlmConstants.fyValue.name()).get("percentage").asText());
    	}else {
    		this.operatingInc = Float.parseFloat(opi.get(VlmConstants.ttmValue.name()).get("value").asText());
    		this.operatingIncPerc = Double.parseDouble(opi.get(VlmConstants.ttmValue.name()).get("percentage").asText());
    	}
    }
    
    @JsonProperty(EMPLOYEES)
    public void unpackEmployeesValue(Map<String,JsonNode> employees) {
    	if(isTTM == 0) {
    		this.employees = Long.parseLong(employees.get(VlmConstants.fyValue.name()).get("value").asText());
    	}else {
    		this.employees = Long.parseLong(employees.get(VlmConstants.ttmValue.name()).get("value").asText());
    	}
    }
   
    @JsonProperty(COUNTRY)
    public void setCountryRegion(Map<String,String> country) throws ClientProtocolException, ExecutionException, IOException {
    	MasterPackageKey regionKey =  CacheManager.getInstance().getDefaultMasterPackageKey(20, VlmConstants.RegionData.name());
    	regionKey.setFirstChildLevelName(VlmConstants.CountryData.name());
    	Map<String,MasterDataGenericRMO> regionList = (Map<String,MasterDataGenericRMO>) CacheManager.getInstance().getCachedObjects(regionKey);
    	
    	regionList.entrySet().forEach((Map.Entry<String, MasterDataGenericRMO> entry)-> {if(entry.getValue().getChildLevel().containsKey(country.get("value"))) {
    		//this.region = entry.getValue().getName();
    		this.country = entry.getValue().getChildLevel().get(country.get("value"));
    	}
    		
    	});
    }
}
