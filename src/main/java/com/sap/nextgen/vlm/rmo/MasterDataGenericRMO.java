package com.sap.nextgen.vlm.rmo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MasterDataGenericRMO {


    public static final String FIELD_ID = "ID";
    public static final String NAME = "Name";


    @JsonProperty(FIELD_ID)
    private String id;

    @JsonProperty(NAME)
    private String name;
    
}
