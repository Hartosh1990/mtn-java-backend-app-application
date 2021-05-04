package com.sap.nextgen.vlm.rmo;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sap.ida.eacp.nucleus.data.client.annotation.Dimension;
import com.sap.ida.eacp.nucleus.data.client.annotation.Measure;
import com.sap.nextgen.vlm.utils.NullHandlingUtility;

import lombok.Data;

@JsonIgnoreProperties("isTTM")
@Data
public class MTNTrendAnalysisKpiRMO {
	
	@JacksonInject("allYrs")
	private List<String> allYrs;
	
	@JacksonInject("gvnYrs")
	private List<String> gvnYrs;
	
	
	@JsonProperty("name")
//	@Dimension(label="Name")
	private String name;
	
	@JsonProperty("Year1")
	@Measure(label = "Year1", numberOfDecimalPlaces = 2)
	private Double kpiValue1;// = this.givenYrskpiValues.get(0);
	

	@JsonProperty("Year2")
	@Measure(label = "Year2",numberOfDecimalPlaces = 2)
	private Double kpiValue2;// = this.givenYrskpiValues.get(1);
	

	@JsonProperty("Year3")
	@Measure(label = "Year3",numberOfDecimalPlaces = 2)
	private Double kpiValue3; //= this.givenYrskpiValues.get(2);
	

	@JsonProperty("Year4")
	@Measure(label = "Year4",numberOfDecimalPlaces = 2)
	private Double kpiValue4; //= this.givenYrskpiValues.get(3);
	

	@JsonProperty("Year5")
	@Measure(label = "Year5",numberOfDecimalPlaces = 2)
	private Double kpiValue5; //= this.givenYrskpiValues.get(4);
	
	@JsonProperty("id")
	@Dimension(label="Kpis", textColumn = "name")
	private Integer kpi;
	
	@JsonProperty("decimalPlaces")
	private Integer decimalPlaces;
	
	@JsonProperty("isCurrency")
	private Integer isCurrency;
	
	
	@JsonProperty("kpiData")
	public void unpackKPIsValues(JsonNode kpiData) throws IOException {
		if(kpiData != null && kpiData.isArray() && kpiData.get(0)!= null && kpiData.get(0).get("data") != null) {
			ObjectMapper mapper = new ObjectMapper();
			ObjectReader objReader = mapper.readerFor(new TypeReference<List<Double>>() {});	
			List<Double> kpiValues = objReader.readValue(kpiData.get(0).get("data"));
			
			int index0 = NullHandlingUtility.getIndex(gvnYrs, allYrs , 0);
			int index1 = NullHandlingUtility.getIndex(gvnYrs, allYrs , 1);
			int index2 = NullHandlingUtility.getIndex(gvnYrs, allYrs , 2);
			int index3 = NullHandlingUtility.getIndex(gvnYrs, allYrs , 3);
			int index4 = NullHandlingUtility.getIndex(gvnYrs, allYrs , 4);
			
			kpiValue1 = index0 >= 0 && index0 < 5 ? kpiValues.get(index0):null;
			kpiValue2 = index1 >= 0 && index1 < 5 ? kpiValues.get(index1):null;
			kpiValue3 = index2 >= 0 && index2 < 5 ? kpiValues.get(index2):null;
			kpiValue4 = index3 >= 0 && index3 < 5 ? kpiValues.get(index3):null;
			kpiValue5 = index4 >= 0 && index4 < 5 ? kpiValues.get(index4):null;
			
		}
		
	}
	
}
