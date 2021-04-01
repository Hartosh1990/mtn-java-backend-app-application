package com.sap.nextgen.vlm.cache.apis;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public interface ICacheServiceLoader<K,V> {
	
	final String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
	public V getDataFromVLM(K key) throws ClientProtocolException, IOException;
}
