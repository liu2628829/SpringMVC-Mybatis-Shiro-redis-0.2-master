package com.sojson.common.utils.responsedata;

import java.util.HashMap;
import java.util.Map;

public class ResponseData {

	private final String message;
	private final int code;
	private final Map<String, Object> data = new HashMap<String, Object>();
	
	public String getMessage() {
		return message;
	}
	
	public int getCode() {
		return code;
	}
	
	public Map<String, Object> getData(String key, Object value) {
		return data;
	}
	
	public ResponseData putDataValue(String key, Object value) {
		data.put(key, value);
		return this;
	}
	
	public ResponseData(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public static ResponseData ok() {
		return new ResponseData(200, "Ok");
	}
	
	public static ResponseData notFound() {
		return new ResponseData(404, "Not Found");
	}
	
	public static ResponseData badRequest() {
		return new ResponseData(400, "Bad Request");
	}
	
	public static ResponseData forbidden() {
		return new ResponseData(403, "Forbidden");
	}
	
	public static ResponseData Unauthoried() {
		return new ResponseData(401, "Unauthorized");
	}
	
	public static ResponseData serverInternalError() {
		return new ResponseData(500, "Server Internal Error");
	}
	
	public static ResponseData customerError() {
		return new ResponseData(1001, "Customer Error");
	}
}
