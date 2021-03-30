package com.sap.nextgen.vlm.utils;

public class MasterPackageKey{
	int langId;
	int packVer;
	int packageId;
	String listName;
	
	@Override
	public boolean equals(Object masterPackage) {
		if(masterPackage == this) {
			return true;
		}
		if(!(masterPackage instanceof MasterPackageKey)) {
			return false;
		}
		MasterPackageKey mpk = (MasterPackageKey) masterPackage;
		
		return Integer.compare(this.langId, mpk.langId) == 0 && 
				Integer.compare(this.packageId, mpk.packageId) == 0 &&
				Integer.compare(this.packVer, mpk.packVer) == 0;
	}
	
}