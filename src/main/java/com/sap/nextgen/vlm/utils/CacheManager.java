package com.sap.nextgen.vlm.utils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sap.nextgen.vlm.cache.apis.ICacheServiceLoader;
import com.sap.nextgen.vlm.cache.apis.impl.IndustryDataLoaderImpl;
import com.sap.nextgen.vlm.cache.apis.impl.MasterDataLoaderImpl;
import com.sap.nextgen.vlm.constants.VlmConstants;
import com.sap.nextgen.vlm.rmo.IndustryDataRMO;
import com.sap.nextgen.vlm.rmo.MasterDataGenericRMO;

public class CacheManager<K,V> {
	
	private static CacheManager singletonInstance;
	
	ICacheServiceLoader<MasterPackageKey, List<MasterDataGenericRMO>> masterDataLoader = new MasterDataLoaderImpl<MasterPackageKey, List<MasterDataGenericRMO>>();
	ICacheServiceLoader<String, List<IndustryDataRMO>> industryDataLoader = new IndustryDataLoaderImpl<String, List<IndustryDataRMO>>();
	
	LoadingCache<K,V> metadata = CacheBuilder.newBuilder().expireAfterAccess(24,TimeUnit.HOURS)
			   .recordStats()
		       .build(new CacheLoader<K,V>() {
		 		@Override
		    	@SuppressWarnings("unchecked")
				public V load(K key) throws ClientProtocolException, IOException {
		 			   if(key instanceof MasterPackageKey){
							return (V)masterDataLoader.getDataFromVLM((MasterPackageKey) key);
					   }else if(key.equals(VlmConstants.bcIndustry.toString())){
						   	return (V)industryDataLoader.getDataFromVLM((String)key);
					   }
		 			   return null;
		 		   }
		 		 });
	public V getCachedObjects(K key) throws ExecutionException, ClientProtocolException, IOException{
		return metadata.get(key);
	}
	public long getCacheHitCount() {
		return metadata.stats().hitCount();
	}
	
	public MasterPackageKey getDefaultMasterPackageKey(int packId,String listName) {
		MasterPackageKey mpk = new MasterPackageKey();
		mpk.setLangId(10); // As of now only english
		mpk.setListName(listName);
		mpk.setPackVer(0); // As it will always give latest data.
		mpk.setPackageId(packId);
		return mpk;
	}
	
	public static CacheManager getInstance() {
		if(singletonInstance == null) {
			singletonInstance = new CacheManager();
		}
		return singletonInstance;
	}
	
}
	

