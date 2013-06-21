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
import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@Entity
@Table(name="USER",uniqueConstraints=@UniqueConstraint(columnNames={"username"}))
@NamedQueries({
    @NamedQuery(name=UserEntity.QUERY_FIND_USER_BY_PWDHASH,
                query="SELECT u FROM UserEntity u WHERE u.passwordHash = :passwordHash"),
    @NamedQuery(name=UserEntity.QUERY_GET_USER_BY_EXT_ID,
                query="SELECT u FROM UserEntity u WHERE u.extUserId = :extUserId"),
    @NamedQuery(name=UserEntity.QUERY_GET_USER_BY_USERNAME,
                query="SELECT u FROM UserEntity u WHERE u.username = :username")
}) 
public class UserEntity {

	public static final String QUERY_FIND_USER_BY_PWDHASH = "QUERY_FIND_USER_BY_PWDHASH";
	public static final String QUERY_GET_USER_BY_EXT_ID = "QUERY_GET_USER_BY_EXT_ID";
	public static final String QUERY_GET_USER_BY_USERNAME = "QUERY_GET_USER_BY_USERNAME";
	
	@Id
	@GeneratedValue
	private long id;
	
	private String extUserId = UUID.randomUUID().toString();
	
	@OneToMany(mappedBy="user", fetch=FetchType.EAGER)
	private Set<SubscriptionEntity> subscriptions;
	
	@OneToMany(mappedBy="user", fetch=FetchType.EAGER)
	private Set<EventPurchaseEntity> events;

	private Date createdOn = new Date();
	
	private String username;
    
    private String passwordHash;
    
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
    

	public long getId() {
		return id;
	}

	public String getExtUserId() {
		return extUserId;
	}

	/**
	 * @return the subscriptions
	 */
	public Set<SubscriptionEntity> getSubscriptions() {
		return subscriptions;
	}

	/**
	 * @param subscriptions the subscriptions to set
	 */
	public void setSubscriptions(Set<SubscriptionEntity> subscriptions) {
		this.subscriptions = subscriptions;
	}
	
	/**
	 * @return the events
	 */
	public Set<EventPurchaseEntity> getEvents() {
		return events;
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(Set<EventPurchaseEntity> events) {
		this.events = events;
	}

	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
}
