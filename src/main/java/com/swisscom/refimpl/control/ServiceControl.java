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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import com.swisscom.refimpl.entity.ServiceEntity;
import com.swisscom.refimpl.entity.SubscriptionEntity;
import com.swisscom.refimpl.events.ServiceChangedEvent;
import com.swisscom.refimpl.events.ServiceChangedEvent.Operation;
import com.swisscom.refimpl.model.Service;
import com.swisscom.refimpl.util.DurationUnit;
import com.swisscom.refimpl.util.ObjectUtil;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@Named
@Singleton
public class ServiceControl {
	
	// object mapper is thread safe
	// see: http://wiki.fasterxml.com/JacksonBestPracticeThreadSafety
	protected final static ObjectMapper MAPPER = new ObjectMapper(); // can reuse, share globally
	
	private Service currentService;
	
	private List<Service> services;
	
	@Inject
	private EntityManager em;

    @Inject 
    Event<ServiceChangedEvent> events;
	
	public static final int MAX_NR_OF_SERVICES = 100;
	

	
	
	public int getMaxNumberOfServices() {
		return MAX_NR_OF_SERVICES;
	}
	

	@Inject
	Logger log;
	
	@PostConstruct
	void init(){
		loadServices();
		currentService = new Service();
	}
	
	
	public void loadServices() {
		if (services == null) {
			synchronized (ServiceControl.class) {
				if (services == null) {
					services = new ArrayList<Service>();
					List<ServiceEntity> res = em.createNamedQuery(ServiceEntity.QUERY_GET_ALL_SERVICES, 
							ServiceEntity.class)
							.setMaxResults(MAX_NR_OF_SERVICES)
							.getResultList();
					for (ServiceEntity s : res) {
						Service sr = entityToDomain(s);
						services.add(sr);
					}
					Collections.sort(services, Service.COMPARATOR_BY_CREATED_ON);
				}
			}
		} 
	}
	
	
	public String saveService() {
		if (currentService != null) {
			synchronized (ServiceControl.class) {
				ServiceEntity sr = domainToEntity(currentService);
				em.persist(sr);
				log.info("service saved: "+sr);
				
				// add service to collection
				Service s = new Service();
				ObjectUtil.copyValues(currentService,s);
				s.setExtId(sr.getExtServiceId());
				s.setCreatedOn(sr.getCreatedOn());
				services.add(0,s);
				
				// notify observers (current users) about service is available
				events.fire(new ServiceChangedEvent(Operation.ADD_AVAILABLE_FOR_PURCHASE, s));
			}
			
		}
		return "manage";
	}
	
	public Service getCurrentService() {
		return currentService;
	}


	public void setCurrentService(Service currentService) {
		this.currentService = currentService;
	}


	public List<Service> getServices() {
		return services;
	}


	public void setServices(List<Service> services) {
		this.services = services;
	}


	/**
	 * @param id
	 * @return
	 */
	public Service retrieveServiceByExtId(String extId) {
		if (extId != null) {
			try {
				log.info("retrive service by ext id: "+extId);
				ServiceEntity e = em.createNamedQuery(ServiceEntity.QUERY_GET_SERVICE_BY_EXT_ID, ServiceEntity.class)
						.setParameter("extServiceId", extId)
						.getSingleResult();
				return entityToDomain(e);
			} catch (NoResultException e) {
				// just return null to FE (jsf) --> empty page
				// this can only happen, if user tries to manipulate the extId
				log.debug(e.getMessage());
			}
		} 
		return null;
		
	}


	/**
	 * @param easypayAuthId
	 */
	public void cancelUserSubscriptionByAuthId(String easypayAuthId, Date cancelledOn) {
		log.info("retrive subscription by auth ext id: "+easypayAuthId);
		SubscriptionEntity e = em.createNamedQuery(SubscriptionEntity.QUERY_GET_SUBSCRIPTION_BY_EASYPAY_AUTH_ID, SubscriptionEntity.class)
				.setParameter("easypayAuthId", easypayAuthId)
				.getSingleResult();
		e.setCancelledOn(cancelledOn);
	}


	/**
	 * @param easypayAuthId
	 */
	public SubscriptionEntity retrieveUserSubscriptionByAuthId(String easypayAuthId) {
		log.info("retrive subscription by auth ext id: "+easypayAuthId);
		SubscriptionEntity e = em.createNamedQuery(SubscriptionEntity.QUERY_GET_SUBSCRIPTION_BY_EASYPAY_AUTH_ID, SubscriptionEntity.class)
				.setParameter("easypayAuthId", easypayAuthId)
				.getSingleResult();
		return e;
	}


	/**
	 * @param easypayAuthId
	 */
	public void deleteUserSubscriptionByAuthId(String easypayAuthId) {
		log.info("retrive subscription by auth ext id: "+easypayAuthId);
		SubscriptionEntity e = em.createNamedQuery(SubscriptionEntity.QUERY_GET_SUBSCRIPTION_BY_EASYPAY_AUTH_ID, SubscriptionEntity.class)
				.setParameter("easypayAuthId", easypayAuthId)
				.getSingleResult();
		em.remove(e);
	}


	/**
	 * @param s
	 * @return
	 */
	public Service entityToDomain(ServiceEntity s) {
		Service sr = new Service();
		sr.setAmount(s.getAmount()+"");
		sr.setExtId(s.getExtServiceId());
		sr.setCreatedOn(s.getCreatedOn());
		sr.setDurationUnit(s.getDurationUnit().toString());
		sr.setPaymentInfo(s.getPaymentInfo());
		sr.setDuration(s.getDuration());
		sr.setAabstract(s.getAabstract());
		sr.setTitle(s.getTitle());
		sr.setName(s.getName());
		//sr.setTransactionId(s.getT)
		return sr;
	}



	/**
	 * @return
	 */
	public ServiceEntity domainToEntity(Service s) {
		ServiceEntity sr = new ServiceEntity();
		sr.setAmount(new BigDecimal(s.getAmount()));
		sr.setPaymentInfo(s.getPaymentInfo());
		if (s.getDuration() !=null && s.getDuration() >0 && s.getDurationUnit() != null) {
			sr.setDuration(s.getDuration());
			sr.setDurationUnit(DurationUnit.valueOf(s.getDurationUnit().toString().toUpperCase()));
		} else {
			sr.setDurationUnit(DurationUnit.EVENT);
		}
		sr.setAabstract(s.getAabstract());
		sr.setTitle(s.getTitle());
		sr.setName(s.getName());
		return sr;
	}


	public void deleteServiceByExtId(String id) {
		// delete the recurrent service
		try {
			log.info("retrive service by ext service id: "+id);
			ServiceEntity se = em.createNamedQuery(ServiceEntity.QUERY_GET_SERVICE_BY_EXT_ID, ServiceEntity.class)
					.setParameter("extServiceId", id)
					.getSingleResult();
			Service s = new Service();
			s.setExtId(se.getExtServiceId());
			em.remove(se);
			events.fire(new ServiceChangedEvent(Operation.SERVICE_DELETED, s));
		} catch (NoResultException e) {
			log.warn("service not found, id="+id);
		}
		// forces to reload the services
		services = null;
		loadServices();
		
	}
	
	
}
