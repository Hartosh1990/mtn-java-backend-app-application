package com.sap.nextgen.vlm.cache.apis.impl;

import java.io.IOException;
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
import com.sap.nextgen.vlm.rmo.IndustryDataRMO;
import com.sap.nextgen.vlm.utils.JWTTokenFactory;

public class IndustryDataLoaderImpl<K,V> implements ICacheServiceLoader<K, V> {
	
	JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
	
	@SuppressWarnings("unchecked")
	public V getDataFromVLM(K key) throws ClientProtocolException, IOException {
		  CloseableHttpClient httpclient = HttpClients.createDefault();
		  //MasterPackageKey typedKey  = ((MasterPackageKey)key);
		  HttpGet get = new HttpGet(baseUri +"/services/bcscope/getbcindustry?language="+10+"&langId="+10+"&clientProcessId=202103301115288g6v832hd4yz&seqNo="+1);
		  get.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		  get.setHeader("Cookie", "UserData="+jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee")); 	
		  CloseableHttpResponse httpResponse;
		  httpResponse = httpclient.execute(get);
		  String response = EntityUtils.toString(httpResponse.getEntity());
		  ObjectMapper mapper = new ObjectMapper();
		  JsonNode root = mapper.readTree(response);
		  Map<String,IndustryDataRMO> industryDataList = new HashMap<String,IndustryDataRMO>();
		  
		  if(root.get("results") != null) {
			 JsonNode metadataList = root.get("results");  
			 if(metadataList.isArray()) {
				 System.out.print("This is an array");
				 for(JsonNode metadataData : metadataList) {
					 IndustryDataRMO indRMO = mapper.treeToValue(metadataData,IndustryDataRMO.class);
					 industryDataList.put(indRMO.getId(),indRMO);
				 }
			 }
		  }
		  
		  return (V)industryDataList;
	}
}
