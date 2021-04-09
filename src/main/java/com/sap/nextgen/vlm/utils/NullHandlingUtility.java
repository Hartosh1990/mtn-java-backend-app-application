package com.sap.nextgen.vlm.utils;

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
}
