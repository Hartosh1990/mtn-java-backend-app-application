package com.sap.nextgen.vlm.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by d059173 on 02.05.17.
 */
public class MathUtils {
    public static Double div(Double val1, Double val2) {
        if(val1 == null || val1 == 0){
            return 0d;
        }
        if (val2 == null || val2 == 0) {
            return null;
        }
        return val1 / val2;
    }

    public static Double div(Integer val1, Integer val2){
        if(val1 == null || val1 == 0){
            return 0d;
        }
        if (val2 == null || val2 == 0) {
            return null;
        }
        return ((double) val1) / ((double) val2);
    }

    public static Double sub(Double val1, Double val2) {
        return diff(val1, val2);
    }

    public static Double add(Double val1, Double val2) {
        return sum(val1, val2);
    }

    public static Double pow(Double val1, int val2) {
        if (val1 == null) {
            return null;
        }
        return Math.pow(val1, val2);
    }

    public static Double sum(Double... values) {
        return Stream.of(values)
                .mapToDouble(d -> d == null ? 0 : d)
                .sum();
    }

    public static Double diff(Double... values){
        return Stream.of(values)
                .mapToDouble(d -> d == null ? 0 : d)
                .reduce((d1, d2) -> d1 - d2)
                .orElse(0d);
    }

    public static Double mult (Double val1 , Double val2) {
        if (val1 == null || val2 == null) {
            return null;
        }
        return val1 * val2;
    }
    
    public static Map.Entry<String, Double> gethighestNumber(Map<String,Double> values) {
    	if(!values.isEmpty()) {
    		return Collections.max(values.entrySet(),(item1,item2) -> item1.getValue() > item2.getValue()?1:-1);	
    	}
    	return null;
    	
    }
    
    public static Map.Entry<String, Double> getLowestNumber(Map<String,Double> values) {
    	if(!values.isEmpty()) {
    		return Collections.min(values.entrySet(),(item1,item2) -> item1.getValue() > item2.getValue()?1:-1);	
    	}
    	return null;
    }
}
