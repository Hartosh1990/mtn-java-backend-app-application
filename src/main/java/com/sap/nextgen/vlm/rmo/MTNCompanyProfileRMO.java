package com.sap.nextgen.vlm.rmo;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.sap.ida.eacp.nucleus.data.client.annotation.Dimension;
import com.sap.ida.eacp.nucleus.data.client.annotation.Measure;
import com.sap.nextgen.vlm.constants.DenominationConstants;
import com.sap.nextgen.vlm.constants.VlmConstants;
import com.sap.nextgen.vlm.utils.CacheManager;
import com.sap.nextgen.vlm.utils.MasterPackageKey;

import lombok.Data;


@Data
@JsonIgnoreProperties(
value = {"ciq_id","mtnId", "isTTM", "companyId", "isPeerDataAvailable", "isQuesDataAvailable","isTrendAnalysisAvailable", "mtnName",
		"region"//,"updatedDate","operInc","employees", "revenue", "industry",
		//"fyUpdatedDate", "denomination",  "country"
		}
)

public class MTNCompanyProfileRMO {
	
	public static final String COMPANYNAME = "companyName";
	public static final String CURRENCY = "currency";
	public static final String INDUSTRY = "industry";
	public static final String OPERATING_INCOME = "operInc";
	public static final String REVENUE = "revenue";
	public static final String EMPLOYEES = "employees";
	public static final String ISTTM = "isTTM";
	public static final String FY_PERIOD_DATE = "fyUpdatedDate";
	public static final String TTM_PERIOD_DATE = "updatedDate";
	public static final String DENOMINATION = "denomination";
	public static final String COUNTRY = "country";
	
	private SimpleDateFormat inputDateFormatter = new SimpleDateFormat("yyyy-mm-dd");
	private SimpleDateFormat outputDateFormatter = new SimpleDateFormat("mm/dd/yyyy");
	private short isTTM = 0;
	
    @JsonProperty(COMPANYNAME)
    @Dimension(label="Company Name")
    private String companyName;
    
    /*This is placeholder for currency value embedded in currency object coming in input Json*/
    @JsonProperty("currencyValue")
    @Dimension(label="Currency")
    private String currency;
    
    @JsonProperty("industryValue")
    @Dimension(label="Industry")
    private String industry;
    
    @JsonProperty("revenueValue")
    @Measure(label="Revenue", numberOfDecimalPlaces = 1)
    private float revenue;
    
    @JsonProperty("opValue")
    @Measure(label="Operating Income",numberOfDecimalPlaces = 1)
    private float operatingInc;
    
    @JsonProperty("employeesValue")
    @Measure(label="Employees",numberOfDecimalPlaces = 0)
    private long employees;
    
    @JsonProperty("periodDateValue")
    @Dimension(label="Period Date")
    private String periodDate;
    
    @JsonProperty("denominationValue")
    @Dimension(label="Denomination")
    private String denomination;
    
    @JsonProperty("regionValue")
    @Dimension(label="Region")
    private String region;

    @JsonProperty("countryValue")
    @Dimension(label="Country")
    private String country;
    
    @JsonProperty("businessDesc")
    @Dimension(label="Business Description")
    private String businessDesc;
    
    @JsonProperty(CURRENCY)
    public void unpackCurrencyValue(Map<String,Integer> currencyMap) throws ClientProtocolException, ExecutionException, IOException {
    	try {
    	Map<String,MasterDataGenericRMO> currencyList = (Map<String,MasterDataGenericRMO>) CacheManager.getInstance().getCachedObjects(CacheManager.getInstance().getDefaultMasterPackageKey(30, VlmConstants.CurrencyData.name()));
    	this.currency = currencyList.get(currencyMap.get("value").toString()).getName();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    @JsonProperty(ISTTM)
    public void getIsTTMFlag(short isTTM) {
    	this.isTTM = isTTM;
    }
    
    @JsonProperty(REVENUE)
    public void unpackRevenueValue(Map<String,JsonNode> revenue) {
    	if(isTTM == 0) {
    		this.revenue = Float.parseFloat(revenue.get(VlmConstants.fyValue.name()).get("value").asText());
    	}else {
    		this.revenue = Float.parseFloat(revenue.get(VlmConstants.ttmValue.name()).get("value").asText());
    	}
    }
    
    @JsonProperty(OPERATING_INCOME)
    public void unpackOpValue(Map<String,JsonNode> opi) {
    	if(isTTM == 0) {
    		this.operatingInc = Float.parseFloat(opi.get(VlmConstants.fyValue.name()).get("value").asText());
    	}else {
    		this.operatingInc = Float.parseFloat(opi.get(VlmConstants.ttmValue.name()).get("value").asText());
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
    @JsonProperty(INDUSTRY)
    public void unpackIndustryValue(Map<String,String> industry) throws ClientProtocolException, ExecutionException, IOException {
    	try {
    	Map<String,IndustryDataRMO> indList = (Map<String,IndustryDataRMO>) CacheManager.getInstance().getCachedObjects(VlmConstants.bcIndustry.toString());
    	this.industry = indList.get(industry.get("value").toString()).getName();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    @JsonProperty(FY_PERIOD_DATE)
    public void setFyPeriodDate(String fy_period_date) throws ParseException {
    	if(this.isTTM == 0) {
    		this.periodDate = outputDateFormatter.format(inputDateFormatter.parse(fy_period_date));	
    	}
    }
    
    @JsonProperty(TTM_PERIOD_DATE)
    public void setTTMPeriodDate(String ttm_period_date) throws ParseException {
    	if(this.isTTM == 1) {
    		this.periodDate = outputDateFormatter.format(inputDateFormatter.parse(ttm_period_date));	
    	}
    }
    
    @JsonProperty(DENOMINATION)
    public void setDenominationValue(short denomination) {
    	this.denomination = DenominationConstants.values()[denomination].name();
    }
    
    @JsonProperty(COUNTRY)
    public void setCountryRegion(Map<String,String> country) throws ClientProtocolException, ExecutionException, IOException {
    	MasterPackageKey regionKey =  CacheManager.getInstance().getDefaultMasterPackageKey(20, VlmConstants.RegionData.name());
    	regionKey.setFirstChildLevelName(VlmConstants.CountryData.name());
    	Map<String,MasterDataGenericRMO> regionList = (Map<String,MasterDataGenericRMO>) CacheManager.getInstance().getCachedObjects(regionKey);
    	
    	regionList.entrySet().forEach((Map.Entry<String, MasterDataGenericRMO> entry)-> {if(entry.getValue().getChildLevel().containsKey(country.get("value"))) {
    		this.region = entry.getValue().getName();
    		this.country = entry.getValue().getChildLevel().get(country.get("value"));
    	}
    		
    	});
    }
}
