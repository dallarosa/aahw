package org.letstalktech.aahw;


import java.util.HashMap;
import java.util.Map;

public class Parameters {
	private String path;
	private Map<String, Object> params;
	private Map<String, Object> headers;
	private Callback callback;
	private String userAgent;

	
	public Parameters(){
		params = new HashMap<String, Object>();
		headers = new HashMap<String, Object>();
		path = "";
		userAgent = "";
	}
	
	public Parameters(String pathValue){
		params = new HashMap<String, Object>();
		path = pathValue;
	}
	
	public Parameters(Map<String, Object> paramsValue){
		params = paramsValue;
		path = "";
	}

	public Parameters(String pathValue, Map<String, Object> paramsValue){
		params = paramsValue;
		path = pathValue;
	}
	
	public void setParameter(String key, Object value){
		params.put(key, value);
	}
	
	public void setHeader(String key, Object value){
		headers.put(key, value);
	}
	
	public void setPath(String value){
		path = value;
	}
	
	public void setCallback(Callback value){
		callback = value;
	}
	
	public Callback getCallback(){
		return callback;
	}
	
	
	public Map<String, Object> getParams(){
		return params;
	}
	
	public String getPath(){
		return path;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}
