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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.swisscom.refimpl.model.AuthSubscriptionDomain;
import com.swisscom.refimpl.util.Constants;
import com.swisscom.rest.security.RequestSignInformations;
import com.swisscom.rest.security.Signer;


/**
 * 
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 *
 *
 */
@Stateless
public class MIB3Client {

	public static final String AUTHSUBSCRIPTIONS_URL = Constants.CE_BASE_URL + "/authsubscriptions";
	public static final String PAYMENTS_URL = Constants.CE_BASE_URL + "/payments";

	public static final SimpleDateFormat RFC_822_DATE_FORMAT_TZ_GMT = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
	
	 // Create an HttpClient with the ThreadSafeClientConnManager.
    // This connection manager must be used if more than one thread will
    // be using the HttpClient.
    //PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
    //cm.setMaxTotal(10);
	//HttpClient httpclient = new DefaultHttpClient(cm);

    HttpClient httpClient = new DefaultHttpClient();
    
	@PostConstruct
    public void init() {
		try {
			TrustSelfSignedStrategy trustStat = new TrustSelfSignedStrategy();
			X509HostnameVerifier allHostsVerifier = new AllowAllHostnameVerifier();
	//		ProtocolSocketFactory socketFactory =
	//			    new EasySSLProtocolSocketFactory( );
	//		Protocol https = new Protocol( "https", socketFactory, 443);
	//		Protocol.registerProtocol( "https", https );
		
			SSLSocketFactory socketFactory;
				socketFactory = new SSLSocketFactory(trustStat, allHostsVerifier);
			
			Scheme sch = new Scheme("https", 443, socketFactory);
			httpClient.getConnectionManager().getSchemeRegistry().register(sch);
		
		} catch (Exception e) {
			new RuntimeException(e.getMessage());
		} 
	}


	private void addSignature(HttpRequestBase request, String methodByName,
			String path, String merchantId, String contentType, byte[] data) {
		RequestSignInformations reqSign = new RequestSignInformations();
		reqSign.setDate(RFC_822_DATE_FORMAT_TZ_GMT.format(new Date()));
		reqSign.setMethod(methodByName);
		reqSign.setPath(path);
		reqSign.setData(data);
		reqSign.setContentType(contentType);

		if (merchantId != null) {
			request.addHeader("x-merchant-id", merchantId);
		}

		String sig;
		try {
			sig = new Signer().buildSignature(reqSign, Constants.SEC_KEY);
			request.addHeader("x-scs-signature", sig);
			request.addHeader("x-scs-date", reqSign.getDate());
			if (data != null) {
				request.addHeader("content-md5",
						new String(Base64.encodeBase64(DigestUtils.md5(data))));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public EasypayResponse modifyAuthSubscription(String merchantId,
			String easypayAuthId, String operation)
					throws IOException, HttpException{
		HttpPut method = null;
		try  {
			method = new HttpPut(MIB3Client.AUTHSUBSCRIPTIONS_URL + "/"
					+ easypayAuthId);
			String entity = "{\"operation\": \""+operation+"\"}";
			method.setEntity(new StringEntity(entity,
					ContentType.create("application/vnd.ch.swisscom.easypay.authsubscription+json","UTF-8")));
			addSignature(method, "PUT", "/authsubscriptions/" + easypayAuthId,
					merchantId, "application/vnd.ch.swisscom.easypay.authsubscription+json",
					entity.getBytes("UTF-8"));
			HttpResponse response = httpClient.execute(method);
			return new EasypayResponse(new String(EntityUtils.toByteArray(response.getEntity())), response.getStatusLine().getStatusCode());
		} finally {
			if (method != null) {
				method.releaseConnection();
			};
		}
	}


	public EasypayResponse modifyTransaction(String merchantId,
			String transactionId, String operation)
			throws IOException, HttpException {
		HttpPut method = null;
		try  {
			method = new HttpPut(MIB3Client.PAYMENTS_URL + "/"
					+ transactionId);
			String entity = "{\"operation\": \""+operation+"\"}";
			method.setEntity(new StringEntity(entity,
					ContentType.create("application/vnd.ch.swisscom.easypay.direct.payment+json","UTF-8")));
			addSignature(method, "PUT", "/payments/" + transactionId,
					merchantId, "application/vnd.ch.swisscom.easypay.direct.payment+json",
					entity.getBytes("UTF-8"));
			HttpResponse response = httpClient.execute(method);
			return new EasypayResponse(new String(EntityUtils.toByteArray(response.getEntity())), response.getStatusLine().getStatusCode());
		} finally {
			if (method != null) {
				method.releaseConnection();
			};
		}
	}
	
	public AuthSubscriptionDomain retrieveAuthSubscription(String merchantId,
			String easypayAuthId)
					throws IOException, HttpException {
		HttpGet method = new HttpGet(MIB3Client.AUTHSUBSCRIPTIONS_URL + "/" + easypayAuthId);
		//out.add(method);
		addSignature(method, "GET", "/authsubscriptions/" + easypayAuthId, merchantId,
				"application/vnd.ch.swisscom.easypay.authsubscription+json", null);
		HttpResponse response = httpClient.execute(method);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(EntityUtils.toByteArray(response.getEntity()),	AuthSubscriptionDomain.class);
	}

}
