package org.letstalktech.aahw;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.graphics.BitmapFactory;

public class PUTRequest extends HTTPRequest {
	HttpPut put;
	public PUTRequest(CookieStore cookieStore, Callback cb){
		super(cookieStore,cb);
	}

	public PUTRequest(CookieStore cookieStore, Callback cb, Callback errorCallback){
		super(cookieStore,cb,errorCallback);
	}
	
	public PUTRequest(){
		super();
	}

	public PUTRequest(CookieStore cookieStore){
		super(cookieStore);
	}

	protected Result doInBackground(Parameters... parameters){
		Result result = new Result();
		android.util.Log.v("PUTRequest","Entered the POSTRequest");
		try {
			String putURL = serverAddress+parameters[0].getPath();
			put = new HttpPut(putURL); 
			android.util.Log.e("PUTRequest",putURL);
//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			for (Map.Entry<String, Object> e : parameters[0].getParams().entrySet())
//			{
//				params.add(new BasicNameValuePair(e.getKey(), (String)e.getValue()));
//			}
//
//			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			if(parameters[0].getUserAgent().length() > 0)
				put.getParams().setParameter(CoreProtocolPNames.USER_AGENT, parameters[0].getUserAgent());
			
			//List<NameValuePair> params = new ArrayList<NameValuePair>();
			MultipartEntity ent = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			Charset chars = Charset.forName("UTF-8");
			
			for (Map.Entry<String, Object> e : parameters[0].getParams().entrySet())
			{
				put.addHeader(e.getKey(), e.getValue().toString());
			}
			
			for (Map.Entry<String, Object> e : parameters[0].getParams().entrySet())
			{
			//	params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
				if(e.getValue().getClass().getSimpleName().contentEquals("File"))
					ent.addPart(e.getKey(),new FileBody((File)e.getValue()));
				else{
				StringBody test = new StringBody(e.getValue().toString(),"text/plain",chars);
				ent.addPart(e.getKey(),test);
				}
				
			}
			put.setEntity(ent);
			HttpResponse responsePUT = httpclient.execute(put,localContext);  

			result.setStatus(responsePUT.getStatusLine().getStatusCode());
			android.util.Log.v("PUTRequest",String.valueOf(result.getStatus()));
			HttpEntity resEntity = responsePUT.getEntity();
			if (resEntity != null) {
				try{
					android.util.Log.v("PUTRequest", response.getHeaders("Content-Type")[0].getValue());
					if(response.getHeaders("Content-Type")[0].getValue().contains("image")){
						android.util.Log.e("PUTRequest","I'm an image!");
						BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(resEntity);
						InputStream is = bufHttpEntity.getContent();
						result.setResponse(BitmapFactory.decodeStream(is));
					}
					else
						if(response.getHeaders("Content-Type")[0].getValue().contains("json")){
							String responseString = EntityUtils.toString(resEntity);
							Object jsonObject  = createJsonObject(responseString);
							result.setResponse(jsonObject);
						}
				} catch (NullPointerException e){
					//e.printStackTrace();
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
//	protected void onPostExecute(Result result){
//		callback.run(result);
//	}
}
