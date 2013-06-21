/*
 * Copyright 2010-2012 swisscom.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 *
 * This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.swisscom.refimpl.boundary;

/**
 * 
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 *
 *
 */
public class EasypayResponse {
	String body;
	int httpCode;
	
	public EasypayResponse() {
	}
	
	
	
	public EasypayResponse(String body, int httpCode) {
		super();
		this.body = body;
		this.httpCode = httpCode;
	}



	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public int getHttpCode() {
		return httpCode;
	}
	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}
	
}
