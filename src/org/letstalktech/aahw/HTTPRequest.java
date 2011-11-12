package org.letstalktech.aahw;


import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class HTTPRequest extends AsyncTask<Parameters,Object, Result> { 
	protected String serverAddress;
	protected HttpContext localContext;
	protected DefaultHttpClient httpclient;
	protected HttpEntityEnclosingRequestBase request;
	protected HttpResponse response;
	protected ResponseHandler<String> responseHandler;
	protected Callback callback;
	protected Callback errorCallback;
	private static final int DEFAULT_TIMEOUT = 10000;
	private CookieStore cookieStore;
//	protected Commons common;
	protected HttpParams httpParams;

	public HTTPRequest(){
		super();
		httpclient = new DefaultHttpClient();

		httpParams = httpclient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_TIMEOUT);
//		httpclient.setHttpRequestRetryHandler(myRetryHandler);
		httpclient.setRedirectHandler(new RedirectHandler() {
			public URI getLocationURI(HttpResponse response,
					HttpContext context) throws ProtocolException {
				return null;
			}

			public boolean isRedirectRequested(HttpResponse response,
					HttpContext context) {
				return false;
			}
		});
		
		localContext = new BasicHttpContext();

		responseHandler = new BasicResponseHandler();

	}

	public HTTPRequest(CookieStore cookieStore){
		super();
		httpclient = new DefaultHttpClient();
		
		httpParams = httpclient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_TIMEOUT);
//		httpclient.setHttpRequestRetryHandler(myRetryHandler);
		httpclient.setRedirectHandler(new RedirectHandler() {
			public URI getLocationURI(HttpResponse response,
					HttpContext context) throws ProtocolException {
				return null;
			}

			public boolean isRedirectRequested(HttpResponse response,
					HttpContext context) {
				return false;
			}
		});
		localContext = new BasicHttpContext();
		this.cookieStore = cookieStore;
		localContext.setAttribute(ClientContext.COOKIE_STORE, this.cookieStore);
		responseHandler = new BasicResponseHandler();
	}

	public HTTPRequest(CookieStore cookieStore, Callback cb){
		super();
		httpclient = new DefaultHttpClient();
		
		httpParams = httpclient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_TIMEOUT);
//		httpclient.setHttpRequestRetryHandler(myRetryHandler);
		httpclient.setRedirectHandler(new RedirectHandler() {
			public URI getLocationURI(HttpResponse response,
					HttpContext context) throws ProtocolException {
				return null;
			}

			public boolean isRedirectRequested(HttpResponse response,
					HttpContext context) {
				return false;
			}
		});
		localContext = new BasicHttpContext();
		this.cookieStore = cookieStore;
		localContext.setAttribute(ClientContext.COOKIE_STORE, this.cookieStore);
		responseHandler = new BasicResponseHandler();
		callback = cb;
	}
	
	public HTTPRequest(CookieStore cookieStore, Callback callback, Callback errorCallback){
		super();
		httpclient = new DefaultHttpClient();
		
		httpParams = httpclient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_TIMEOUT);
//		httpclient.setHttpRequestRetryHandler(myRetryHandler);
		httpclient = new DefaultHttpClient();
		httpclient.setRedirectHandler(new RedirectHandler() {
			public URI getLocationURI(HttpResponse response,
					HttpContext context) throws ProtocolException {
				return null;
			}

			public boolean isRedirectRequested(HttpResponse response,
					HttpContext context) {
				return false;
			}
		});
		localContext = new BasicHttpContext();
		this.cookieStore = cookieStore;
		localContext.setAttribute(ClientContext.COOKIE_STORE, this.cookieStore);
		responseHandler = new BasicResponseHandler();
		this.callback = callback;
		this.errorCallback = errorCallback;
	}
	
	protected Object createJsonObject(String jsonString){
	if(jsonString.startsWith("[{")){
		Log.v("POSTRequest", "is array");
		JSONArray jsonArray = new JSONArray(); 
		
		try {
			jsonArray = new JSONArray(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return(jsonArray);
	}
	else{
		Log.v("POSTRequest", "is object");
		JSONObject jsonObject = new JSONObject();
		try {

			android.util.Log.e("POSTRequest",jsonString);

			jsonObject=new JSONObject(jsonString);

			//								android.util.Log.e("POSTRequest",json.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return(jsonObject);
	}
	}	
	
	public void setHeaders(Set<Entry<String,Object>> headerList){
		for (Map.Entry<String, Object> e : headerList)
		{
			request.addHeader(e.getKey(), e.getValue().toString());
		}
	}

	public void setCallback(Callback cb)  {
		callback = cb;
	}


	public void setServerAddress(String value){
		serverAddress = value;
	}

	public String getServerAddress(){
		return serverAddress;
	}

	@Override
	protected Result doInBackground(Parameters... params) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void onPostExecute(Result result){
		if(!isCancelled())
		{
			android.util.Log.v("HTTPRequest",String.valueOf(result.getStatus()));
			if(result.getStatus() < 400)
			{
				if(callback != null)
					callback.run(result);
			}
			else
			{
				if(errorCallback != null)
					errorCallback.run(result);
			}
			
		}
		else
		{
			if(errorCallback != null)
				errorCallback.run(result);
		}
	}

	public void setConnectionTimeout(int timeout)
	{
		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
	}

	public void setSoTimeout(int timeout)
	{
		HttpConnectionParams.setSoTimeout(httpParams, timeout);
	}
	
	public Callback getErrorCallback() {
		return errorCallback;
	}

	public void setErrorCallback(Callback errorCallback) {
		this.errorCallback = errorCallback;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
		localContext.setAttribute(ClientContext.COOKIE_STORE, this.cookieStore);
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}
	

	
}
