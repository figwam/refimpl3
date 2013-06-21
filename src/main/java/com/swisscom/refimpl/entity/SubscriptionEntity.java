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
package com.swisscom.refimpl.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@Entity
@Table(name="SUBSCRIPTION")
@NamedQueries({
    @NamedQuery(name=SubscriptionEntity.QUERY_GET_SUBSCRIPTION_BY_EASYPAY_AUTH_ID,
            query="SELECT s FROM SubscriptionEntity s WHERE s.easypayAuthId = :easypayAuthId"),
    @NamedQuery(name=SubscriptionEntity.QUERY_GET_SUBSCRIPTION_BY_EXT_SERVICE_ID,
            query="SELECT s FROM SubscriptionEntity s WHERE s.service.extServiceId = :extServiceId"),
    @NamedQuery(name=SubscriptionEntity.QUERY_GET_ACTIVE_SUBSCRIPTION_BY_NEXT_PAYMENT,
            query="SELECT s FROM SubscriptionEntity s " +
                		"WHERE s.nextPayment <= :nextPayment " +
                		"AND s.cancelledOn is NULL ")
}) 
public class SubscriptionEntity {


	public static final String QUERY_GET_SUBSCRIPTION_BY_EASYPAY_AUTH_ID = "QUERY_GET_SUBSCRIPTION_BY_EASYPAY_AUTH_ID";

	public static final String QUERY_GET_SUBSCRIPTION_BY_EXT_SERVICE_ID = "QUERY_GET_SUBSCRIPTION_BY_EXT_SERVICE_ID";
	
	public static final String QUERY_GET_ACTIVE_SUBSCRIPTION_BY_NEXT_PAYMENT = "QUERY_GET_ACTIVE_SUBSCRIPTION_BY_NEXT_PAYMENT";
	
	@Id
	@GeneratedValue
	private long id;
	
	private String easypayAuthId;
	
	private String transactionId;
	
	private Date nextPayment;
	
	private String extSubscriptionId = UUID.randomUUID().toString();

	private Date createdOn = new Date();

	private Date cancelledOn;

	private String orderId;

    @ManyToOne
	private UserEntity user;

    @ManyToOne
	private ServiceEntity service;

	public String getEasypayAuthId() {
		return easypayAuthId;
	}

	public void setEasypayAuthId(String easypayAuthId) {
		this.easypayAuthId = easypayAuthId;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public ServiceEntity getService() {
		return service;
	}

	public void setService(ServiceEntity service) {
		this.service = service;
	}

	public long getId() {
		return id;
	}

	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @return the extSubscriptionId
	 */
	public String getExtSubscriptionId() {
		return extSubscriptionId;
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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Date getNextPayment() {
		return nextPayment;
	}

	public void setNextPayment(Date nextPayment) {
		this.nextPayment = nextPayment;
	}

	@Override
	public String toString() {
		return "SubscriptionEntity [id=" + id + ", easypayAuthId="
				+ easypayAuthId + ", transactionId=" + transactionId
				+ ", nextPayment=" + nextPayment + ", extSubscriptionId="
				+ extSubscriptionId + ", createdOn=" + createdOn
				+ ", cancelledOn=" + cancelledOn + "]";
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	
}
