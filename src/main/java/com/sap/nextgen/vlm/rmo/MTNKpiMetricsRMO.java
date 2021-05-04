package com.sap.nextgen.vlm.rmo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.sap.ida.eacp.nucleus.data.client.annotation.Dimension;
import com.sap.ida.eacp.nucleus.data.client.annotation.Measure;
import com.sap.nextgen.vlm.constants.ImpactArea;
import com.sap.nextgen.vlm.constants.MeasuringUnit;
import com.sap.nextgen.vlm.constants.VlmConstants;
import com.sap.nextgen.vlm.utils.MathUtils;
import com.sap.nextgen.vlm.utils.NullHandlingUtility;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonIgnoreProperties(
value = {"id", "infoText"})

@JsonPropertyOrder({
    "kpiName",
    "laggingPeerName",
    "laggingPeerValue",
    "companyPerformance",
    "leadingPeerName",
    "leadingPeerValue",
    "benefitLevel",
    "monetryBenefit",
    "impactArea",
    "isHigher",
    "fcfSum",
    "oISum",
    "isCurrency",
    "decimalPlaces",
    "benefitUnitName"
})
@Accessors(chain = true)
public class MTNKpiMetricsRMO {


	public static final String COMPANIES = "companies"; 
    public static final String NAME = "name";
    public static final String IS_HIGHER = "isHigher";
    public static final String IS_CURRENCY = "isCurrency";
    public static final String DECIMALPLACES = "decimalPlaces";
    public static final String IMPACT_ID = "impactID";
    public static final String BENEFIT_LEVEL_ID =  "benefitLevelID";
    
   
    @JsonProperty(NAME)
    @Dimension(label="Kpis" , rank = 0)
    private String kpiName;
   
    @JsonProperty("lggingPeerName")
    @Dimension(label="Lagging Peer", rank = 1)
    private String laggingPeerName;
    

    @JsonProperty("lggingPeerValue")
    @Measure(label="Lagging Peer Value", rank = 2,numberOfDecimalPlaces = 2)
    private Double laggingPeerValue;
    
    @JsonProperty("companyPerformance")
    @Measure(label="Company Perform.", rank = 3 ,numberOfDecimalPlaces = 2)
    private Double companyPerformance ;
    
    @JsonProperty("leadingPeerName")
    @Dimension(label="Leading Peer", rank = 4)
    private String leadingPeerName;
    
    
    @JsonProperty("leadingPeerValue")
    @Measure(label="Leading Peer Value", rank = 5,numberOfDecimalPlaces = 2)
    private Double leadingPeerValue;
    

    @JsonProperty("benefitLevel")
    @Dimension(label="Benefit Level", rank = 6)
    private String benefitLevel;
    
    @JsonProperty("monetryBenefit")
    @Measure(label="Potential Value", rank = 7, numberOfDecimalPlaces = 2)
    private Double monetryBenefit;
    
    @JsonProperty("impactArea")
    @Dimension(label = "Impact Area", rank = 8)
    private String impactArea;
    
    @JsonProperty(IS_HIGHER)
    @Dimension(label = "Is Higher The Better", rank = 9, isVisible = false)
    private Integer isHigher;
    
    @JsonProperty("cummulativeBenefitInFCF")
    @Measure(label="FCF Sum", rank = 10, isVisible = false,numberOfDecimalPlaces = 2)
    private Double fcfSum = 0.0;
    
    @JsonProperty("cummulativeBenefitInOI")
    @Measure(label="oI Sum", rank = 11, isVisible = false, numberOfDecimalPlaces = 2)
    private Double oISum = 0.0;
    
    
    @JsonProperty(IS_CURRENCY)
    private String isCurrency;
    
    @JsonProperty(DECIMALPLACES)
    private int decimalPlaces;
    
    private String benefitUnitName;
    
    @JsonProperty(BENEFIT_LEVEL_ID)
    public void getBenefitLevelString(int benefitLevelId) {
    	this.benefitUnitName = MeasuringUnit.getUnit(benefitLevelId);
    } 
    

    @JsonProperty(IMPACT_ID)
    public void getImpactAreaString(Integer impactId) {
    	if(impactId != null) {
    		this.impactArea = ImpactArea.values()[impactId].name();	
    	}
    }
    
    @JsonProperty(COMPANIES)
    public void getLeadingPeerCompany(JsonNode companies) {
    	Map<String,Double> peersKpiValue = new HashMap<String,Double>();
    	
    	companies.forEach((c->{
    		JsonNode baselineInfo = null;
    		Boolean isBaseCompany =	c.get(VlmConstants.isBaseCompany.name()).asBoolean();
    		String isTTM = c.get(VlmConstants.isTTM.name()).asText();
    		if(!"1".equals(isTTM)) {
    			Double companyPerformace =  NullHandlingUtility.parseDouble(c.get(VlmConstants.fyValue.name()).get(VlmConstants.kpiValue.name()));
    			if(companyPerformace != null) {
    				peersKpiValue.put(c.get("name").asText(), companyPerformace);	
    			}
    			if(isBaseCompany) {
    				this.companyPerformance = companyPerformace;	
    				baselineInfo = c.get(VlmConstants.fyValue.name()).get(VlmConstants.baselineInfo.name());
    			}
    			
    		} 
    		else {
    			Double companyPerformance = NullHandlingUtility.parseDouble(c.get(VlmConstants.ttmValue.name()).get(VlmConstants.kpiValue.name()));
    			if(companyPerformance != null) {
    				peersKpiValue.put(c.get("name").asText(),companyPerformance);	
    			}
    			if(isBaseCompany) {
    				this.companyPerformance = companyPerformance;	
    				baselineInfo = c.get(VlmConstants.ttmValue.name()).get(VlmConstants.baselineInfo.name());
    			}
    		}
    		if(isBaseCompany && baselineInfo.isArray() && baselineInfo.get(0) != null) {
    			Long isCalReq = NullHandlingUtility.parseLong(baselineInfo.get(0).get("isBenefitCal"));
    			if(isCalReq == 1) {
    				Double value  = NullHandlingUtility.parseDouble(baselineInfo.get(0).get("value"));
    				Double factor = NullHandlingUtility.parseDouble(baselineInfo.get(0).get("benefitFactor"));	
    				this.monetryBenefit = value != null && factor != null ? value * factor : null;
    				this.benefitLevel =  MeasuringUnit.getFormattedBenefitFactor(factor, benefitUnitName);
    				if(ImpactArea.FCF.name().equals(impactArea)) {
    					this.fcfSum = this.fcfSum + (this.monetryBenefit != null ? this.monetryBenefit : 0.0);
    				}else if(ImpactArea.OI.name().equals(impactArea)) {
    					this.oISum = this.oISum + (this.monetryBenefit != null ? this.monetryBenefit : 0.0);
    				}
    			}	
    		}
    		
    	}));
    	if(!peersKpiValue.isEmpty()) {
    		Map.Entry<String,Double> highestValuePeerEnrty = MathUtils.gethighestNumber(peersKpiValue);
        	Map.Entry<String,Double> lowestValuePeerEnrty = MathUtils.getLowestNumber(peersKpiValue);
        	if(this.isHigher != null && this.isHigher == 1) {
        		
        		this.leadingPeerName = highestValuePeerEnrty.getKey();
        		this.leadingPeerValue = highestValuePeerEnrty.getValue();
        		this.laggingPeerName = lowestValuePeerEnrty.getKey();
        		this.laggingPeerValue = lowestValuePeerEnrty.getValue();
        		
        	}else {
        		
        		this.leadingPeerName = lowestValuePeerEnrty.getKey();
        		this.leadingPeerValue = lowestValuePeerEnrty.getValue();
        		this.laggingPeerName = highestValuePeerEnrty.getKey();
        		this.laggingPeerValue = highestValuePeerEnrty.getValue();
        	}	
    	}
    	
    	
    }
    
}
