package com.sap.nextgen.vlm.cache.apis.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.nextgen.vlm.cache.apis.ICacheServiceLoader;
import com.sap.nextgen.vlm.constants.VlmConstants;
import com.sap.nextgen.vlm.rmo.MasterDataGenericRMO;
import com.sap.nextgen.vlm.utils.MasterPackageKey;

public class MasterDataLoaderImpl<K,V> implements ICacheServiceLoader<K, V> {

	@SuppressWarnings("unchecked")
	@Override
	public V getDataFromVLM(K key) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub

		  CloseableHttpClient httpclient = HttpClients.createDefault();
		  MasterPackageKey typedKey  = ((MasterPackageKey)key);
		  HttpGet get = new HttpGet(baseUri +"/services/common/getmasterdata?language="+typedKey.getLangId()+"&langId="+typedKey.getLangId()
		  +"&packid="+typedKey.getPackageId()+"&packver="+typedKey.getPackVer()+"&clientProcessId=202103301115288g6v832hd4yz&seqNo="+1);
		  get.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		  //get.setHeader("Cookie", "UserData="+jwtTokenFactory.getToken()); 	
		  CloseableHttpResponse httpResponse;
		  httpResponse = httpclient.execute(get);
		  String response = EntityUtils.toString(httpResponse.getEntity());
		  ObjectMapper mapper = new ObjectMapper();
		  JsonNode root = mapper.readTree(response);
		  
		  Map<String,MasterDataGenericRMO> masterDataList = new HashMap<String,MasterDataGenericRMO>();
		  
		  if(root.get("results").get(0) != null) {
			 JsonNode metadataList = root.get("results").get(0).get("masterdataList");  
			 if(metadataList.isArray()) {
				 System.out.print("List is of type Array");
				 for(JsonNode metadataData : metadataList) {
					MasterDataGenericRMO md = mapper.treeToValue(metadataData.get(typedKey.getListName()).get("level_info"),MasterDataGenericRMO.class);
					masterDataList.put(md.getId(),md);
					if(typedKey.getFirstChildLevelName() != null && metadataData.get(VlmConstants.childLevel.name()).isArray() && !metadataData.get(VlmConstants.childLevel.name()).isEmpty()) {
						for(JsonNode firstChildMetadata : metadataData.get(VlmConstants.childLevel.name())){
							JsonNode firstChildInfoNode = firstChildMetadata.get(0).get(typedKey.getFirstChildLevelName()).get(VlmConstants.level_info.name());
							MasterDataGenericRMO childMd = mapper.treeToValue(firstChildInfoNode,MasterDataGenericRMO.class);
							md.getChildLevel().put(childMd.getId(),childMd.getName());
						}
					}
				 }
			 }
		  }
		  httpclient.close();
		  httpResponse.close();
		  return (V)masterDataList;
	
	}

}
