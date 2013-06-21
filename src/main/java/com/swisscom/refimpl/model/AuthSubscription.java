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

/**
 * The service model class for the SUBSCRIPTION.
 * 
 */
public class AuthSubscription extends Payment implements Serializable {
	
	public enum Operation {COMMIT, REJECT, CANCEL}

	private static final long serialVersionUID = 1L;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Integer duration;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String durationUnit;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String nextPayment;		
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String operation;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Boolean isActive;

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	public String getNextPayment() {
		return nextPayment;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}	
		


}