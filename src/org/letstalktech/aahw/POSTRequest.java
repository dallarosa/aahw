package org.letstalktech.aahw;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.BitmapFactory;

public class POSTRequest extends HTTPRequest {
	
	private HttpMultipartMode mode;
	public final static int URL_ENCODED_FORM = 0;
	public final static int MULTIPART = 1;
	//HttpPost request;
	public POSTRequest(CookieStore cookieStore, Callback cb){
		super(cookieStore,cb);
	}
	public POSTRequest(CookieStore cookieStore, Callback cb, Callback errorCallback){
		super(cookieStore,cb,errorCallback);
	}

	public POSTRequest(){
		super();
	}

	public POSTRequest(CookieStore cookieStore){
		super(cookieStore);
	}

	@Override	
	protected Result doInBackground(Parameters... parameters) {
		Result result = new Result();
		android.util.Log.v("POSTRequest","Entered the POSTRequest");
		try {
			String postURL = serverAddress+parameters[0].getPath();
			request = new HttpPost(postURL); 
			android.util.Log.e("POSTRequest",postURL);
			
			if(parameters[0].getUserAgent().length() > 0){
				request.getParams().setParameter(
						CoreProtocolPNames.USER_AGENT, 
						parameters[0].getUserAgent());
			}
			setHeaders(parameters[0].getHeaders().entrySet());
			
			MultipartEntity ent = createMultipartEntityFromParameters(parameters[0].getParams().entrySet());
			
			request.setEntity(ent);
			response = httpclient.execute(request,localContext);

			result.setStatus(response.getStatusLine().getStatusCode());
			android.util.Log.v("POSTRequest",String.valueOf(result.getStatus()));
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {    
				try{
					android.util.Log.v("POSTRequest", response.getHeaders("Content-Type")[0].getValue());
					if(response.getHeaders("Content-Type")[0].getValue().contains("image")){
						android.util.Log.e("POSTRequest","I'm an image!");
						BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(resEntity);
						InputStream is = bufHttpEntity.getContent();
						result.setResponse(BitmapFactory.decodeStream(is));
					}
					else if(response.getHeaders("Content-Type")[0].getValue().contains("json")){
							JSONObject json = null;
							try {
								String teste = EntityUtils.toString(resEntity);
								android.util.Log.e("POSTRequest",teste);
								json=new JSONObject(teste);
							
//								android.util.Log.e("POSTRequest",json.toString());
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							result.setResponse(json);
						}
						else if(response.getHeaders("Content-Type")[0].getValue().contains("text/html")){
								result.setResponse(EntityUtils.toString(resEntity));
								android.util.Log.e("POSTRequest",(String) result.getResponse());
							}
						else {
							 InputStream contentStream = resEntity.getContent();
							 byte[] content = null;
							 contentStream.read(content);
							 result.setResponse(content);
						}
					
					
				}catch(NullPointerException e){
					result.setResponse(new String("null"));
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	protected MultipartEntity createMultipartEntityFromParameters(Set<Entry<String,Object>> parameterList) throws UnsupportedEncodingException{
		
		MultipartEntity ent = new MultipartEntity((mode == null) ? HttpMultipartMode.BROWSER_COMPATIBLE : mode);
		
		Charset chars = Charset.forName("UTF-8");
		
		for (Map.Entry<String, Object> e : parameterList)
		{
		//	params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
			if(e.getValue().getClass().getSimpleName().contentEquals("File"))
				ent.addPart(e.getKey(),new FileBody((File)e.getValue()));
			else{
			StringBody test = new StringBody(e.getValue().toString(),"text/plain",chars);
			ent.addPart(e.getKey(),test);
			}
			
		}
		return ent;

		//UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
		//post.addHeader("Content-Type", "multipart/form-data");

	}

	protected UrlEncodedFormEntity createUrlEncodedFormEntityFromParameters(Set<Entry<String,Object>> parameterList) throws UnsupportedEncodingException{
		//MultipartEntity ent = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		for (Map.Entry<String, Object> e : parameterList)
		{
			params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
		}

		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
		return ent;
		//post.addHeader("Content-Type", "multipart/form-data");

	}
	
	public HttpMultipartMode getMode() {
		return mode;
	}
	public void setMode(HttpMultipartMode mode) {
		this.mode = mode;
	}
	
//	protected void onPostExecute(Result result){
//		android.util.Log.v("POSTRequest",String.valueOf(result.getStatus()));
//		if(callback != null)
//			callback.run(result);
//	}
}
