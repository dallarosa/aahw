package org.letstalktech.aahw;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.BitmapFactory;
import android.util.Log;

public class POSTRequest extends HTTPRequest {

	//Form content types
	public final static int URL_ENCODED_FORM = 0;
	public final static int MULTIPART = 1;
	public final static int FILE_IN_BODY = 2;

	private HttpMultipartMode mode;
	private int formContentType = 0;
	public int getFormContentType() {
		return formContentType;
	}
	public void setFormContentType(int formContentType) {
		this.formContentType = formContentType;
	}
	private OAuthConsumer consumer = null;

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

			switch(formContentType){
			case MULTIPART:
				
				MultipartEntity multipartEnt = createMultipartEntityFromParameters(parameters[0].getParams().entrySet());
				Log.v("POSTRequest", "I'm multipart");
				request.setEntity(multipartEnt);
				if(consumer != null){
					consumer.sign(request);
				}
				break;
			case URL_ENCODED_FORM:
				Log.v("POSTRequest", "I'm url encoded");
				UrlEncodedFormEntity urlEncodedEnt = createUrlEncodedFormEntityFromParameters(parameters[0].getParams().entrySet());
				request.setEntity(urlEncodedEnt);
				if(consumer != null){
					consumer.sign(request);
				}
				break;
			case FILE_IN_BODY:
				Log.v("POSTRequest", "I'm file in body");
				FileEntity fileEntity = createFileEntityFromParameters(parameters[0].getParams().entrySet());
				request.setEntity(fileEntity);
				if(consumer != null){
					consumer.sign(request);
				}
			}

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
						String responseString = EntityUtils.toString(resEntity);
						Object jsonObject  = createJsonObject(responseString);
						result.setResponse(jsonObject);

					}
					else if(response.getHeaders("Content-Type")[0].getValue().contains("text/html")){
						result.setResponse(EntityUtils.toString(resEntity));
						android.util.Log.e("POSTRequest",(String)result.getResponse());
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
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	protected FileEntity createFileEntityFromParameters(Set<Entry<String,Object>> parameterList){
		FileEntity ent = null;
		for(Map.Entry<String, Object>e : parameterList){
			ent = new FileEntity((File)e.getValue(), e.getKey());
		}
		return(ent);
	}

	protected MultipartEntity createMultipartEntityFromParameters(Set<Entry<String,Object>> parameterList) throws UnsupportedEncodingException, FileNotFoundException{

		MultipartEntity ent = new MultipartEntity((mode == null) ? HttpMultipartMode.BROWSER_COMPATIBLE : mode);
		Charset chars = Charset.forName("UTF-8");

		for (Map.Entry<String, Object> e : parameterList)
		{
			//	params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
			if(e.getValue().getClass().getSimpleName().contentEquals("File")){
				//ent.addPart(e.getKey(),new FileBody((File)e.getValue()));
				 FormBodyPart p2 = new FormBodyPart("userfile",new InputStreamBody(new FileInputStream((File)e.getValue()),"image/jpeg",e.getKey()));
				 ent.addPart(p2);
			}
			else{
				StringBody stringBody = new StringBody(e.getValue().toString(),chars);
				ent.addPart(e.getKey(),stringBody);
			}

		}
		return ent;

	}

	protected UrlEncodedFormEntity createUrlEncodedFormEntityFromParameters(Set<Entry<String,Object>> parameterList) throws UnsupportedEncodingException{
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (Map.Entry<String, Object> e : parameterList)
		{
			params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
		}

		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
		return ent;

	}

	public HttpMultipartMode getMode() {
		return mode;
	}
	public void setMode(HttpMultipartMode mode) {
		this.mode = mode;
	}
	public OAuthConsumer getConsumer() {
		return consumer;
	}
	public void setConsumer(OAuthConsumer consumer) {
		this.consumer = consumer;
	}	
}
