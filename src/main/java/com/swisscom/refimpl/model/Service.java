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
package com.swisscom.refimpl.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class Service implements Serializable {
	
	public enum DurationUnit {
		DAY, WEEK, MONTH, EVENT;
	}
	
	
	private static final long serialVersionUID = 1L;

	public static transient final Comparator<Service> COMPARATOR_BY_CREATED_ON = new Comparator<Service>() {
		@Override
		public int compare(Service o1, Service o2) {
			// oldest first (descending order)
			return o2.getCreatedOn().compareTo(o1.getCreatedOn());
		}
	};
	private String extId;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String easypayAuthId;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String transactionId;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String name;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Integer duration;	

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String aabstract;	

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String title;	

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String durationUnit;	

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Boolean isActive;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String paymentInfo;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Boolean isAdultContent;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Boolean isRoaming;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String amount;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Date createdOn;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Date cancelledOn;
	
	public Service() {
		createdOn = new Date();
	}


	public void setExtId(String extId) {
		this.extId = extId;
	}


	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}


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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public Boolean getIsAdultContent() {
		return isAdultContent;
	}

	public void setIsAdultContent(Boolean isAdultContent) {
		this.isAdultContent = isAdultContent;
	}

	public Boolean getIsRoaming() {
		return isRoaming;
	}

	public void setIsRoaming(Boolean isRoaming) {
		this.isRoaming = isRoaming;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public String getExtId() {
		return extId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}
	
	public String getHumanReadableDuration(){
		if (durationUnit.equals("WEEK")) {
			return "Weekly";
		} else if (durationUnit.equals("MONTH")) {
			return "Monthly";
		} else if (durationUnit.equals("DAY")) {
			return "Daily";
		} else {
			return durationUnit;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return extId.equals(((Service)obj).extId);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return extId.hashCode();
	}


	/**
	 * @return the easypayAuthId
	 */
	public String getEasypayAuthId() {
		return easypayAuthId;
	}


	/**
	 * @param easypayAuthId the easypayAuthId to set
	 */
	public void setEasypayAuthId(String easypayAuthId) {
		this.easypayAuthId = easypayAuthId;
	}


	/**
	 * @return the cancelledOn
	 */
	public Date getCancelledOn() {
		return cancelledOn;
	}


	/**
	 * @param cancelledOn the cancelledOn to set
	 */
	public void setCancelledOn(Date cancelledOn) {
		this.cancelledOn = cancelledOn;
	}


	/**
	 * @return the validTill
	 */
	public Date getValidTill() {
		return addDuration(createdOn, cancelledOn, durationUnit, duration);
	}


	/**
	 * @return the validTill
	 */
	public Date getNextPayment() {
		return addDuration(createdOn, new Date(), durationUnit, duration);
	}


	/**
	 * @return the validTill
	 */
	public Date addDuration(Date start, Date stop, String durationUnit, Integer duration) {
		if (stop != null) {
			GregorianCalendar gcCreated = new GregorianCalendar();
			gcCreated.setTime(start);
			GregorianCalendar gcCancelled = new GregorianCalendar();
			gcCancelled.setTime(stop);
			while(gcCreated.before(gcCancelled)) {
				if (durationUnit.equals(DurationUnit.WEEK.toString())) {
					gcCreated.add(GregorianCalendar.HOUR, duration*7*24);
				} else if (durationUnit.equals(DurationUnit.DAY.toString())) {
					gcCreated.add(GregorianCalendar.HOUR, duration*24);
				} else if (durationUnit.equals(DurationUnit.MONTH.toString())){
					gcCreated.add(GregorianCalendar.MONTH, duration);
				} else {
					break;
				}
			}
			return gcCreated.getTime();
		} else {
			return null;
		}
	}
	
	public String getStatus() {
		if (cancelledOn == null) {
			return "ACTIVE";
		} else {
			return "CANCELLED";
		}
	}
	
	public boolean isEvent() {
		boolean hasDuration = (getDuration() != null && getDuration() > 0);
		return  !hasDuration || getDurationUnit() == null;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	public String getAabstract() {
		return aabstract;
	}


	public void setAabstract(String aabstract) {
		this.aabstract = aabstract;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getTransactionId() {
		return transactionId;
	}


	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}
