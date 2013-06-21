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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.swisscom.refimpl.util.DurationUnit;


/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@Entity
@Table(name="SERVICE")
@NamedQueries({
    @NamedQuery(name=ServiceEntity.QUERY_GET_ALL_SERVICES,
                query="SELECT s FROM ServiceEntity s ORDER BY s.createdOn DESC"),
    @NamedQuery(name=ServiceEntity.QUERY_GET_SERVICE_BY_EXT_ID,
                query="SELECT s FROM ServiceEntity s WHERE s.extServiceId = :extServiceId"),
    @NamedQuery(name=ServiceEntity.QUERY_GET_SERVICES_BY_USERNAME,
                query="SELECT s.service FROM SubscriptionEntity s WHERE s.user.username = :username")
}) 
public class ServiceEntity {

	public static final String QUERY_GET_ALL_SERVICES = "QUERY_GET_ALL_SERVICES";
	
	public static final String QUERY_GET_SERVICE_BY_EXT_ID = "QUERY_GET_SERVICE_BY_EXT_ID";
	
	public static final String QUERY_GET_SERVICES_BY_USERNAME = "QUERY_GET_SERVICES_BY_USERNAME";
	
	@Id
	@GeneratedValue
	private long id;
	
	private String extServiceId = UUID.randomUUID().toString();

	@OneToMany(mappedBy="service", orphanRemoval=true)
	private List<SubscriptionEntity> subscriptions;
	
	@OneToMany(mappedBy="service", orphanRemoval=true)
	private List<EventPurchaseEntity> eventPurchases;

	@Enumerated(EnumType.STRING)
	private DurationUnit durationUnit; 

	private BigDecimal amount;

	private String paymentInfo;

	private String name;

	private Integer duration;
	
	@Column(length=500)
	private String aabstract;	

	private String title;

	private Date createdOn = new Date();

	
	public DurationUnit getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(DurationUnit durationUnit) {
		this.durationUnit = durationUnit;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public long getId() {
		return id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public List<SubscriptionEntity> getSubscriptions() {
		return subscriptions;
	}

	@Override
	public String toString() {
		return "Service [id=" + id 
				+ ", durationUnit=" + durationUnit + ", amount=" + amount
				+ ", paymentInfo=" + paymentInfo + ", createdOn=" + createdOn
				+ "]";
	}

	/**
	 * @return the extServiceId
	 */
	public String getExtServiceId() {
		return extServiceId;
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

	public List<EventPurchaseEntity> getEventPurchases() {
		return eventPurchases;
	}

	public void setEventPurchases(List<EventPurchaseEntity> eventPurchases) {
		this.eventPurchases = eventPurchases;
	}
	
	

}
