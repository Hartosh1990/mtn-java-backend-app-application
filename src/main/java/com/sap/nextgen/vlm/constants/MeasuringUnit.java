package com.sap.nextgen.vlm.constants;

public class MeasuringUnit {
	
	public static String NUMBER = " Number";
	public static String DAY = " Day";
	public static String PERCENTAGE = " %";
	public static String PP = " PP";
	public static String KK = "*1K";
	public static String TK = "*10K";

	public static String getUnit(int index) {
		String unit = PERCENTAGE;
		switch(index) {
			case 10:
				unit = NUMBER;
				break;
			case 20:
				unit = DAY;
				break;
			case 30:
				unit = PERCENTAGE;
				break;
			case 40:
				unit = PP;
				break;
			case 50:
				unit = KK;
				break;
			case 60:
				unit = TK;
				break;
				
		} 
		 
		return unit;
	}
	
	public static String getFormattedBenefitFactor(Double factor , String unit) {
		String formattedFactor = null;
		if(factor != null) {
			int intValue = (int) Math.floor(factor);
			if(intValue == factor) {
				
				return intValue+ unit;
			}
			return factor+unit;
		}
		return formattedFactor;
	}

}
