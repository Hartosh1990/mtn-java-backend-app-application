package com.sap.nextgen.vlm.utils;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRequestManager {
	
	
	public static JsonNode getRootObjectFromGetNodeService(String jwtToken, String uri)
			throws IOException, ClientProtocolException, JsonProcessingException, JsonMappingException, SQLException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet get = new HttpGet(uri);
		get.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		get.setHeader("Cookie", "UserData="+jwtToken);
		CloseableHttpResponse httpResponse = httpclient.execute(get);
		String response = EntityUtils.toString(httpResponse.getEntity());
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response); 	
		httpclient.close();
		httpResponse.close();
		return root;
	}
	
	public static JsonNode getRootObjectFromPostNodeService(String jwtToken, String uri, String body)
			throws IOException, ClientProtocolException, JsonProcessingException, JsonMappingException, SQLException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(uri);
		StringEntity entity = new StringEntity(body);
		post.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		post.setHeader("Cookie", "UserData="+jwtToken);
		post.setEntity(entity);
		CloseableHttpResponse httpResponse = httpclient.execute(post);
		String response = EntityUtils.toString(httpResponse.getEntity());
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response); 	
		httpclient.close();
		httpResponse.close();
		return root;
	}

}
