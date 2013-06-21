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
package com.swisscom.refimpl.control;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import com.swisscom.refimpl.boundary.EasypayResponse;
import com.swisscom.refimpl.boundary.MIB3Client;
import com.swisscom.refimpl.entity.EventPurchaseEntity;
import com.swisscom.refimpl.entity.ServiceEntity;
import com.swisscom.refimpl.entity.SubscriptionEntity;
import com.swisscom.refimpl.entity.UserEntity;
import com.swisscom.refimpl.events.ServiceChangedEvent;
import com.swisscom.refimpl.events.ServiceChangedEvent.Operation;
import com.swisscom.refimpl.model.CheckoutRequestItem;
import com.swisscom.refimpl.model.CheckoutResponseItem;
import com.swisscom.refimpl.model.MessageDomain;
import com.swisscom.refimpl.model.MessagesDomain;
import com.swisscom.refimpl.model.Service;
import com.swisscom.refimpl.util.Constants;
import com.swisscom.refimpl.util.DateUtil;
import com.swisscom.rest.security.SignatureException;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@Named
@Stateless
public class UserManager {
	
	// object mapper is thread safe
	// see: http://wiki.fasterxml.com/JacksonBestPracticeThreadSafety
	protected final static ObjectMapper MAPPER = new ObjectMapper(); // can reuse, share globally

	private static final String PURCHASE_STATUS_SUCCESS = "success";

	private static final String PURCHASE_STATUS_ERROR = "error";

	private static final String PURCHASE_STATUS_CANCEL = "cancel";
	
	private static final String OUTCOME_BACK_JSF = "home";
	
	@Inject
	private EntityManager em;

	@Inject
	Logger log;

	@Inject
	ServiceControl serviceControl;

    @Inject 
    Event<ServiceChangedEvent> events;
    
    @Inject
    MIB3Client mib3Client;
    
	
	public UserEntity retrieveUser(String username, String password) {
		try {
			UserEntity user = em.createNamedQuery(UserEntity.QUERY_FIND_USER_BY_PWDHASH, UserEntity.class)
				.setParameter("passwordHash", generateMD5Base64(username, password))
				.getSingleResult();
			return user;
		} catch (NoResultException e) {
			return null;
		}
		
	}
	
	public String delete (String sid, String easypayAuthId) {
		Service s = new Service();
		s.setEasypayAuthId(easypayAuthId);
		s.setExtId(sid);
		events.fire(new ServiceChangedEvent(Operation.REMOVE_USER_PURCHASED, s));
		return "myServices";
	}

	
	public String cancel (String sid, String easypayAuthId) {
		try {
			EasypayResponse response = mib3Client.modifyAuthSubscription(Constants.MERCHANT_ID, easypayAuthId, "CANCEL");
			
			// seems to be some unsynchronisations, anyway update the database
			// handle this case like 200 SUCCESS response
			boolean isUnsyncAlreadyCanceled = response.getHttpCode() == 400 
					&& MAPPER.readValue(response.getBody(), MessagesDomain.class)
					.getMessages().get(0).getCode().equals(MessageDomain.Code.SUBSCRIPTION_ALREADY_CANCELED);
			
			boolean isUnsyncDoesNotExistsOnSCMSide = response.getHttpCode() == 404;
			
			if(response.getHttpCode() == 200 || isUnsyncAlreadyCanceled || isUnsyncDoesNotExistsOnSCMSide) {
				// ok
				Service s = new Service();
				s.setEasypayAuthId(easypayAuthId);
				s.setExtId(sid);
				events.fire(new ServiceChangedEvent(Operation.CANCEL_USER_SUBSCRIPTION_PURCHASED, s));	
				FacesContext.getCurrentInstance()
					.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Service canceled successfully", null));
				return "myServices";
			} else if (response.getHttpCode() == 400) {
				// Business exception
				MessageDomain msg = MAPPER.readValue(response.getBody(), MessagesDomain.class)
						.getMessages().get(0);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getMessage(), null));
			} else {
				// notify customer
				FacesContext.getCurrentInstance()
					.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Some technical problem occured, please try later.", null));
				// write error detail to log
				log.error("Some technical problem occured on easypay.");
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
		} 
		return "myService";
	}

	public String img(int idx, int hash) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"/img/tn" + Math.abs(hash % 11) + ".jpg";
	}

	public String img(String name) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"/img/"+name.toLowerCase()+ "_th.jpg";
	}

	/**
	 * @param username
	 * @param password
	 */
	public void registerUser(String username, String password) {
		if (em.createNamedQuery(UserEntity.QUERY_GET_USER_BY_USERNAME)
				.setParameter("username",username)
				.getResultList()
				.size() > 0) {
			FacesContext.getCurrentInstance().addMessage("ERROR", new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "username already exists",
					"username already exists"));
		} else {
			UserEntity u = new UserEntity();
			u.setUsername(username);
			u.setPasswordHash(generateMD5Base64(username, password));
			em.persist(u);
		}
	}
	
	public void deleteUser(String username) {
		List<UserEntity> ue = em.createNamedQuery(UserEntity.QUERY_GET_USER_BY_USERNAME, UserEntity.class)
				.setParameter("username",username)
				.getResultList();
		if (ue.size() > 0) {
			em.remove(ue.get(0));
		} else {
			FacesContext.getCurrentInstance().addMessage("ERROR", new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "username not found",
					"username not found"));
		}
	}


	public String getPurchaseStatusColor() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();

		String status = params.get("purchase");
		if (status == null) {
			status = "";
		}
		if (status.equals(PURCHASE_STATUS_ERROR)){
			return "red";
		} else if (status.equals(PURCHASE_STATUS_SUCCESS)){
			return "green";
		} else if (status.equals(PURCHASE_STATUS_CANCEL)){
			return "gray";
		} else {
			return "black";
		}
	}


	public String finalizePurchase() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();

		String status = params.get("purchase");
		
		if (status != null) {
			HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			log.info(request.getRequestURI());
			log.info(request.getPathInfo());
			log.info(request.getQueryString());
			
			String uid = params.get("uid");
			String sid = params.get("sid");
			
			log.info("uid: " + uid);
			log.info("sid: " +sid);
		
			if (status.equals(PURCHASE_STATUS_ERROR)){
				String signature = params.get("signature");
				String checkoutResponseItem = params.get("checkoutResponseItem");
				CheckoutResponseItem cr = null;
				if (signature != null && checkoutResponseItem != null) {
					try {
						cr = buildCheckoutResponseItem(checkoutResponseItem, signature);
					} catch (Exception e) {
						log.error("Could not extract response-item: "+e.getMessage());
					} 
				}
				if (cr == null || cr.getReason() == null) {
					return "Service purchase failed cause: "+params.get("error");
				} else {
					return cr.getReason();
				}
			} else if (status.equals(PURCHASE_STATUS_SUCCESS)){
				
				String authSubscriptionId = params.get("authSubscriptionId");
				String paymentId = params.get("paymentId");
				
				// get the user and service
				UserEntity user = em.createNamedQuery(UserEntity.QUERY_GET_USER_BY_EXT_ID, UserEntity.class)
						.setParameter("extUserId", uid)
						.getSingleResult();
				ServiceEntity service = em.createNamedQuery(ServiceEntity.QUERY_GET_SERVICE_BY_EXT_ID, ServiceEntity.class)
						.setParameter("extServiceId", sid)
						.getSingleResult();
				
				// in case of event we dont have an 'authSubscriptionId'
				try {
					if (paymentId != null) {
						EasypayResponse r = mib3Client.modifyTransaction(Constants.MERCHANT_ID, paymentId, "COMMIT");
						log.info("Got response code ["+r.getHttpCode()+"] ");
						if (r.getHttpCode() == 400) {
							log.error("Event could not be commited: "+r);
							return "Service purchase failed.";
						} else if (r.getHttpCode() != 200){
							log.error("Event could not be commited, http response was: "+r.getHttpCode());
							return "Service purchase failed.";
						} else {
							EventPurchaseEntity eventP = new EventPurchaseEntity();
							eventP.setUser(user);
							eventP.setService(service);
							eventP.setTransactionId(paymentId);
							eventP.setOrderId(params.get("orderId"));
							em.persist(eventP);
							
							// notify listeners about user service change
							Service s = serviceControl.entityToDomain(service);
							s.setTransactionId(paymentId);
							s.setCreatedOn(eventP.getCreatedOn());
							events.fire(new ServiceChangedEvent(Operation.ADD_USER_EVENT_PURCHASED, s));
							return "Service purchase was successfull.";
						}
					} else { 
						EasypayResponse r = mib3Client.modifyAuthSubscription(Constants.MERCHANT_ID, authSubscriptionId, "COMMIT");
						log.info("Got response code ["+r.getHttpCode()+"] ");
						if (r.getHttpCode() == 400) {
							log.error("Subscription authorization could not be commited: "+r);
							return "Service purchase failed.";
						} else if (r.getHttpCode() != 200){
							log.error("Subscription authorization could not be commited, http response was: "+r.getHttpCode());
							return "Service purchase failed.";
						} else {
							SubscriptionEntity subscription = new SubscriptionEntity();
							subscription.setService(service);
							subscription.setUser(user);
							subscription.setEasypayAuthId(authSubscriptionId);
							subscription.setTransactionId(paymentId);
							if (service.getDuration() != null && service.getDurationUnit() != null) {
								subscription.setNextPayment(DateUtil.calculateSubscriptionEndDate(new GregorianCalendar(), 
										service.getDurationUnit(), service.getDuration()));
							} else {
								// it event
								subscription.setCancelledOn(new Date());
							}
							em.persist(subscription);
							
							// notify listeners about user service change
							Service s = serviceControl.entityToDomain(service);
							s.setEasypayAuthId(authSubscriptionId);
							s.setCreatedOn(subscription.getCreatedOn());
							events.fire(new ServiceChangedEvent(Operation.ADD_USER_SUBSCRIPTION_PURCHASED, s));
							return "Service purchase was successfull.";
						}
						
					}
				} catch (Exception e){
					// if commit fails
					return "Service purchase failed.";
				}
			} else if (status.equals(PURCHASE_STATUS_CANCEL)){
				return "Service purchase was canceled by customer.";
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	public String doAuthorize(Service serviceToAuthorize, String currentUserId) {
		try {
			CheckoutRequestItem ri = serviceToCheckoutItem(serviceToAuthorize, currentUserId);
			String coItem = MAPPER.writeValueAsString(ri);
			byte[] coItemb64 = Base64.encodeBase64(coItem.getBytes("UTF-8"));
			byte[] signb64 = Base64.encodeBase64(sign(coItem.getBytes("UTF-8"),
					Constants.SEC_KEY.getBytes("UTF-8")));
			String coUrl = chooseProtocol(Constants.MIB3_CO_URL);
			coUrl = coUrl+"?signature="
					+ urlEncode(new String(signb64),false)
					+ "&checkoutRequestItem="
					+ urlEncode(new String(coItemb64),false);
			log.info(coUrl);
			return coUrl;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String chooseProtocol(String mib3CoUrl) {
		List<String> mobileProxyIps = (List<String>)Arrays.asList(Constants.MOBILE_PROXY_IPS.split(","));
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String client_ip = request.getHeaders("x-forwarded-for").hasMoreElements() ? request.getHeaders("x-forwarded-for").nextElement() : null;
		if( client_ip!=null && mobileProxyIps.contains(client_ip) ) {
			log.info("received mobile-proxy request from ip: " + client_ip);
			mib3CoUrl = mib3CoUrl.replace("https", "http");
		}
		return mib3CoUrl;
	}

	private CheckoutRequestItem serviceToCheckoutItem(Service service, String currentUserId) {
		CheckoutRequestItem ri = new CheckoutRequestItem();
		if (service.getPaymentInfo().toLowerCase().contains("adult")) {
			ri.setIsAdultContent(true);
		} else {
			ri.setIsAdultContent(false);
		}
		if (service.getPaymentInfo().toLowerCase().contains("roaming")) {
			ri.setIsRoaming(true);
		} else {
			ri.setIsRoaming(false);
		}
		ri.setAmount(service.getAmount());
		ri.setPaymentInfo(service.getPaymentInfo());
		ri.setMerchantName("RefImpl");
		ri.setMerchantId("CH");
		ri.setDescription(service.getAabstract());
		if(service.getTitle() != null){
			ri.setTitle(service.getTitle());
		} else {
			ri.setTitle(service.getPaymentInfo());
		}
		if (service.getDuration() != null) {
			ri.setDuration(service.getDuration());
		}
		ri.setDurationUnit(service.getDurationUnit().toString());
		ri.setCancelUrl(Constants.REFIMPL_BACK_URL+"/"+OUTCOME_BACK_JSF+".jsf?uid="+currentUserId+"&sid="+service.getExtId()+"&purchase="+PURCHASE_STATUS_CANCEL);
		ri.setErrorUrl(Constants.REFIMPL_BACK_URL+"/"+OUTCOME_BACK_JSF+".jsf?uid="+currentUserId+"&sid="+service.getExtId()+"&purchase="+PURCHASE_STATUS_ERROR);
		ri.setSuccessUrl(Constants.REFIMPL_BACK_URL+"/"+OUTCOME_BACK_JSF+".jsf?uid="+currentUserId+"&sid="+service.getExtId()+"&purchase="+PURCHASE_STATUS_SUCCESS);
		return ri;
	}

	private CheckoutResponseItem buildCheckoutResponseItem(String checkoutResponseItem, String signature) 
			throws JsonParseException, JsonMappingException, IOException {
		CheckoutResponseItem cr = null;
		byte[] signb64;
		try {
			// check signature
			signb64 = Base64.encodeBase64(sign(Base64.decodeBase64(checkoutResponseItem.getBytes("UTF-8")),
					Constants.SEC_KEY.getBytes("UTF-8")));
			String signExpected = new String(signb64,"UTF-8");
			if (!signExpected.equals(signature)) {
				log.error("!!! Signature validation failed !!!, Received: "+signature+" Expected: "+signExpected);
			}
			
			// extract the response
			String decodedResponse = new String (Base64.decodeBase64(checkoutResponseItem.getBytes("UTF-8")), "UTF-8");
			cr = MAPPER.readValue(decodedResponse, CheckoutResponseItem.class);
		} catch (SignatureException e) {
			log.error("!!! Signature validation failed !!!", e.getMessage());
		}
		return cr;
	}

	private byte[] sign(byte[] data, byte[] key) throws SignatureException {
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(key, "HmacSHA1"));
			return mac.doFinal(data);
		} catch (Exception e) {
			throw new SignatureException(
					"Unable to calculate a request signature: "
							+ e.getMessage(), e);
		}
	}
	
	private String urlEncode(String value, boolean path) {
		if (value == null)
			return "";

		try {
			String encoded = URLEncoder.encode(value, "UTF-8")
					.replace("+", "%20")
					.replace("*", "%2A")
					.replace("%7E", "~");
			if (path) {
				encoded = encoded.replace("%2F", "/");
			}

			return encoded;
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private String generateMD5Base64(String username, String password) {
		byte[] bytesOfMessage;
		try {
			bytesOfMessage = new String(username+":"+password).getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			return new String (Base64.encodeBase64(thedigest));
		} catch (UnsupportedEncodingException e) {
			// should never occur, otherwise the JVM is bad configured 
			// and we have a application runtime problem
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			// should never occur, 'MD5' Digest is part of JDK, 
			// otherwise we have a application runtime problem
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		CheckoutRequestItem ri = new CheckoutRequestItem();
		ri.setIsAdultContent(false);
		ri.setIsRoaming(false);
		ri.setAmount("2.25");
		ri.setPaymentInfo("weekly_abo_news");
		ri.setMerchantName("RefImpl");
		ri.setMerchantId("CH");
		ri.setDescription("Read your favorite newspaper every day everywhere!");
		ri.setDuration(1);
		ri.setDurationUnit("WEEK");
		String currentUserId = UUID.randomUUID().toString();
		String sid = UUID.randomUUID().toString();
		ri.setCancelUrl(Constants.REFIMPL_BACK_URL+"/"+OUTCOME_BACK_JSF+".jsf?uid="+currentUserId+"&sid="+sid+"&purchase="+PURCHASE_STATUS_CANCEL);
		ri.setErrorUrl(Constants.REFIMPL_BACK_URL+"/"+OUTCOME_BACK_JSF+".jsf?uid="+currentUserId+"&sid="+sid+"&purchase="+PURCHASE_STATUS_ERROR);
		ri.setSuccessUrl(Constants.REFIMPL_BACK_URL+"/"+OUTCOME_BACK_JSF+".jsf?uid="+currentUserId+"&sid="+sid+"&purchase="+PURCHASE_STATUS_SUCCESS);		
		System.out.println(MAPPER.writeValueAsString(ri));
	}
}