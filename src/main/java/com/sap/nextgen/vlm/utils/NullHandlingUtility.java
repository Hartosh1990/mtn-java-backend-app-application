package com.sap.nextgen.vlm.utils;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class NullHandlingUtility {
	
	public static Double parseDouble(JsonNode value) {
		if(!value.isNull()) {
			return Double.parseDouble(value.asText());
		}
		return null;
	}
	
	public static Float parseFloat(JsonNode value) {
		if(!value.isNull()) {
			return Float.parseFloat(value.asText());
		}
		return null;
	}
	public static Long parseLong(JsonNode value) {
		if(!value.isNull()) {
			return Long.parseLong(value.asText());
		}
		return null;
	}
	
	/* This function will give index of sub set string at given index from full set  
	 */
	public static int getIndex(List<String> subSet, List<String> fullSet, int subSetIndex) {
		if(subSetIndex < subSet.size() && subSet.get(subSetIndex)!=null) {
			return fullSet.indexOf(subSet.get(subSetIndex));	
		}
		return -1;
	}
}
