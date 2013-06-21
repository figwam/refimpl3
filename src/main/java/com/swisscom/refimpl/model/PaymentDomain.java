/*
 (c) Copyright Swisscom (Schweiz) AG. All rights reserved.

 This product is the proprietary and sole property of Swisscom (Schweiz) AG
 Use, duplication or dissemination is subject to prior written consent of
 Swisscom (Schweiz) AG.

 $Id: $

 */
package com.swisscom.refimpl.model;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;


public abstract class PaymentDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String uri;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String paymentInfo;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Boolean isAdultContent;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Boolean isRoaming;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String merchantId;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String amount;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String msisdn;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String createdOn;


	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return the paymentInfo
	 */
	public String getPaymentInfo() {
		return paymentInfo;
	}

	/**
	 * @param paymentInfo the paymentInfo to set
	 */
	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	/**
	 * @return the isAdultContent
	 */
	public Boolean getIsAdultContent() {
		return isAdultContent;
	}

	/**
	 * @param isAdultContent the isAdultContent to set
	 */
	public void setIsAdultContent(Boolean isAdultContent) {
		this.isAdultContent = isAdultContent;
	}

	/**
	 * @return the isRoaming
	 */
	public Boolean getIsRoaming() {
		return isRoaming;
	}

	/**
	 * @param isRoaming the isRoaming to set
	 */
	public void setIsRoaming(Boolean isRoaming) {
		this.isRoaming = isRoaming;
	}

	/**
	 * @return the merchantId
	 */
	public String getMerchantId() {
		return merchantId;
	}

	/**
	 * @param merchantId the merchantId to set
	 */
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	/**
	 * @return the msisdn
	 */
	public String getMsisdn() {
		return msisdn;
	}

	/**
	 * @param msisdn the msisdn to set
	 */
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	@Override
	public String toString() {
		return msisdn;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	
}