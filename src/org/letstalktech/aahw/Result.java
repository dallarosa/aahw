package org.letstalktech.aahw;


public class Result {
	private Object responseContent;
	private int status;
	
	public Result(){
		responseContent = "";
		status = 0;
	}
	
	public Result(String response){
		responseContent = response;
	}
	
	public Result(int statusValue){
		status = statusValue;
	}
	
	public Result(Object response, int statusValue){
		responseContent = response;
		status = statusValue;
	}
	
	public Object getResponse(){
		return responseContent;
	}
	
	public int getStatus(){
		return status;
	}
	
	public void setResponse(Object value){
		responseContent = value;
	}
	
	public void setStatus(int value){
		status = value;
	}
}
