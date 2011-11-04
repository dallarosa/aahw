package org.letstalktech.aahw;


import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.BitmapFactory;

public class GETRequest extends HTTPRequest {

	public GETRequest(CookieStore cookieStore, Callback cb){
		super(cookieStore,cb);
	}
	
	public GETRequest(CookieStore cookieStore, Callback cb, Callback errorCallback){
		super(cookieStore,cb,errorCallback);
	}
	
	public GETRequest(){
		super();
	}
	
	public GETRequest(CookieStore cookieStore){
		super(cookieStore);
	}

	@Override
	protected Result doInBackground(Parameters... parameters){
		Result result = new Result();

			String getURL = serverAddress+parameters[0].getPath();
			android.util.Log.v("GETRequest", getURL);
			HttpGet httpget = new HttpGet(getURL);
			android.util.Log.e("GETRequest",getURL);
			if(parameters[0].getUserAgent().length() > 0)
				httpget.getParams().setParameter(CoreProtocolPNames.USER_AGENT, parameters[0].getUserAgent());
			
			setHeaders(parameters[0].getHeaders().entrySet());
			
			try {
				response = httpclient.execute(httpget, localContext);
				result.setStatus(response.getStatusLine().getStatusCode());
			
				HttpEntity resEntity = response.getEntity();
				if(resEntity != null) {
					for(Header h : response.getAllHeaders()){
						android.util.Log.v("GETRequest", getURL+h.getValue());
					}
					android.util.Log.v("GETRequest", getURL+response.getHeaders("Content-Type")[0].getValue());
					if(response.getHeaders("Content-Type")[0].getValue().contains("octet")||response.getHeaders("Content-Type")[0].getValue().contains("image")){
						android.util.Log.e("GETRequest","I'm an image!");
						BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(resEntity);
						InputStream is = bufHttpEntity.getContent();
						result.setResponse(BitmapFactory.decodeStream(is));
					}
					else
						if(response.getHeaders("Content-Type")[0].getValue().contains("json")){
							JSONObject json = null;
							try {
								String content = EntityUtils.toString(resEntity);
								android.util.Log.v("GETRequest",content);
								json=new JSONObject(content);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							result.setResponse(json);
						}
						else if(response.getHeaders("Content-Type")[0].getValue().contains("ml")){
							String content = EntityUtils.toString(resEntity);
							android.util.Log.v("GETRequest","Content: "+content);
				//			android.util.Log.v("GETRequest","Encoding: "+resEntity.getContentEncoding().toString());
							result.setResponse(content);
						}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				this.cancel(true);
			} catch (IOException e) {
				e.printStackTrace();
				this.cancel(true);
			}
			return result;

	}

}
