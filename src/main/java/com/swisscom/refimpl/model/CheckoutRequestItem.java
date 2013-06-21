/**
 * 
 */
package com.swisscom.refimpl.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class CheckoutRequestItem extends AuthSubscription {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	String errorUrl;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	String cancelUrl;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	String successUrl;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	String merchantName;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	String description;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	String title;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getErrorUrl() {
		return errorUrl;
	}

	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	

}
