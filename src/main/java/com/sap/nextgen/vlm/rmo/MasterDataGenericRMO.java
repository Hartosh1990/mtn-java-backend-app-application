package com.sap.nextgen.vlm.rmo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(
value = {"Order","version","approve"}
)
public class MasterDataGenericRMO {


    public static final String FIELD_ID = "ID";
    public static final String NAME = "Name";


    @JsonProperty(FIELD_ID)
    private String id;

    @JsonProperty(NAME)
    private String name;
    
    private Map<String,String> childLevel = new HashMap<String, String>();
    
}
