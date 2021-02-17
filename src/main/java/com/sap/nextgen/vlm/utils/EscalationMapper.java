package com.sap.nextgen.vlm.utils;

public class EscalationMapper {

    public static String mapProcessTypeText(String processType) {
        switch (processType) {
            case "Critical Customer Situation without handover":
                return "Critical Customer Situation w/o handover";
            case "Business Down Situation":
                return "Business Down";
            default:
                return processType;
        }
    }
}
