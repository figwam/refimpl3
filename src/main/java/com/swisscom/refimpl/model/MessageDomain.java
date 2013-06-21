/**
 (c) Copyright Swisscom (Schweiz) AG. All rights reserved.

 This product is the proprietary and sole property of Swisscom (Schweiz) AG
 Use, duplication or dissemination is subject to prior written consent of
 Swisscom (Schweiz) AG.

 */
package com.swisscom.refimpl.model;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class Message.java.
 * 
 * Example of Message JSON:
 * 
 * <pre>
 * {
 *   "code": "MISSING_MANDATORY_PARAMETER",
 *   "field": "amount",
 *   "message": "the mandatory field amount is missing"
 *   "requestId": "acb-123-fg"
 * }
 * </pre>
 * 
 * @author tgdscald
 */
public class MessageDomain implements Serializable {
	
	public enum Code {
		ACCOUNT_ALREADY_EXISTS("The account already exists."),
		ACCOUNT_DOES_NOT_EXIST("The account does not exist."),
		CE_NOT_AUTHORIZED("The customer is not authorized."),
		MERCHANT_NOT_FOUND("The merchant does not exist."),
		MISSING_MANDATORY_FIELD("A mandatory field is missing in a JSON request object. " +
				"The missing parameter is specified in the field-element of the response message."),
		MISSING_MANDATORY_HEADER_PARAMETER("A mandatory parameter is missing in a HTTP-Header. " +
				"The missing parameter is specified in the field-element of the response message."), 
		MISSING_MANDATORY_QUERY_PARAMETER("A mandatory query parameter is missing in a URL. " +
				"The missing parameter is specified in the field-element of the response message."), 
		INVALID_MSISDN_FORMAT("The given msisdn format is not valid."),
		INVALID_DATE_FORMAT("The given date format is not valid."),
		INVALID_AMOUNT_FORMAT("The given amount or trialAmount format is not valid."),
		RENEWAL_TO_EARLY("The subscription renewal is to early."), 
		SERVICE_NOT_FOUND("The service with specified service id was not found."), 
		SERVICE_DURATION_INVALID("The service duration is invalid."), 
		SUBSCRIPTION_ALREADY_EXISTS("The subscription already exists."),
		TRANSACTION_ALREADY_COMMITTED("The transaction for the subscription was committed already."),
		TRANSACTION_ALREADY_REJECTED("The transaction for the subscription was rejected already."),
		OPERATION_INVALID("The operation on object is invalid."),
		SUBSCRIPTION_STATUS_INVALID("The subscription status is invalid. Allowed values: 'commit', 'reject'."),
		SUBSCRIPTION_ALREADY_CANCELED("The subscription was canceled already."),
		SUBSCRIPTION_INACTIVE("The subscription is inactive. No need to cancel."),
		SUBSCRIPTION_IS_NOT_RECURRENT("The subscription is not of type: recurrent-subscription."),
		TRANSACTION_NOT_COMMITTED("The transaction of the subscription was never committed, " +
				"hence the subscription is not active."),
		NON_UNIQUE_RESULT("The result is not unique, " +
				"The invalid parameter is specified in the field-element."),

		MD5_CHECKSUM_NOT_MATCH("The received content MD5 does not match the calculated content MD5"),
		REQUEST_TIME_ODD("The request time is odd."),
		AUTHORIZATION_ERROR("You are not authorized."),
		METHOD_NOT_ALLOWED("You are not allowed to use these method.");
		
		/**
		 * 
		 */
		private Code(String message) {
			this.message = message;
		}
		
		private String message;

		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * @param message the message to set
		 */
		public void setMessage(String message) {
			this.message = message;
		}
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String message;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String field;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Code code;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String requestId;
	
	

	/**
	 * 
	 */
	private MessageDomain() {
		super();
	}

	/**
	 * @param message
	 * @param description
	 * @param code
	 */
	public MessageDomain(Code code, String message, String field,
			String requestId) {
		this();
		this.message = message;
		this.field = field;
		this.code = code;
		this.requestId = requestId;
	}
	
	public MessageDomain(Code code, String field,
			String requestId) {
		this(code, code.getMessage(), field, requestId);
	}
	
	public MessageDomain(Code code, String field) {
		this(code, code.getMessage(), field, null);
	}
	
	public MessageDomain(Code code) {
		this(code, code.getMessage(), null, null);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * @param requestId
	 *            the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * @return the code
	 */
	public Code getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(Code code) {
		this.code = code;
	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageDomain [message=" + message + ", field=" + field
				+ ", code=" + code + ", requestId=" + requestId + "]";
	}


}
