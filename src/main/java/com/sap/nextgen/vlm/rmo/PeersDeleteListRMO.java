package com.sap.nextgen.vlm.rmo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PeersDeleteListRMO {
	
	List<PeerInfoRMO> peers = new ArrayList<PeerInfoRMO>();

}
