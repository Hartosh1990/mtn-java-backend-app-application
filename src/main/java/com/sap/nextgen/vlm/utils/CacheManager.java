package com.sap.nextgen.vlm.utils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CacheManager {
	
	String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
	
	@Inject
    private JWTTokenFactory jwtTokenFactory;
    
	public List<String> getCachedObjects(MasterPackageKey key) throws ExecutionException, ClientProtocolException, IOException{
		CacheLoader<MasterPackageKey,List<String>> loader = new CacheLoader<MasterPackageKey,List<String>>() {
	 		   public List<String> load(MasterPackageKey key) throws ClientProtocolException, IOException {
	 			  CloseableHttpClient httpclient = HttpClients.createDefault();
	 			  HttpGet get = new HttpGet(baseUri +"/services/common/getmasterdata?language="+key.langId+"&langId="+key.langId+"&packid="+key.packageId+"&packver="+key.packVer+"&clientProcessId=202103301115288g6v832hd4yz&seqNo="+1);
	 			  get.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
	 			  get.setHeader("Cookie", "UserData="+jwtTokenFactory.getToken()); 	
	 			  CloseableHttpResponse httpResponse;
	 			  httpResponse = httpclient.execute(get);
	 			  String response = EntityUtils.toString(httpResponse.getEntity());
	 			  ObjectMapper mapper = new ObjectMapper();
	 			  JsonNode root = mapper.readTree(response);
	 			  JsonNode metadataList ;
	 			  if(root.get("results").get(0) != null) {
	 				  metadataList = root.get("results").get(0).get("masterdataList");  
	 			  }
	 			  
	 			  return List.of("Test1","Test2");
	 		   }
	 		 };
	 		 

	    LoadingCache<MasterPackageKey,List<String>> metadata = CacheBuilder.newBuilder()
			 			       .build(loader);
	 	return metadata.get(key);
	}
}
	

