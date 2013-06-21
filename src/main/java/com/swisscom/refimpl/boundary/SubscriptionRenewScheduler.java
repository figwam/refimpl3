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
package com.swisscom.refimpl.boundary;

import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import com.swisscom.refimpl.entity.SubscriptionEntity;
import com.swisscom.refimpl.model.MessagesDomain;
import com.swisscom.refimpl.util.Constants;
import com.swisscom.refimpl.util.DateUtil;
/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@Singleton
public class SubscriptionRenewScheduler {
	
	@Inject
	Logger log;
	

	@Inject
	private EntityManager em;

    
    @Inject
    MIB3Client mib3Client;
    
	// object mapper is thread safe
	// see: http://wiki.fasterxml.com/JacksonBestPracticeThreadSafety
	protected final static ObjectMapper MAPPER = new ObjectMapper(); // can reuse, share globally
	
	@Schedule(hour = "1", persistent = false)
	public void renewSubscriptionAuthorizations() {
		log.info("renew subscriptions on scm side");
		List<SubscriptionEntity> subs = em.createNamedQuery(SubscriptionEntity.QUERY_GET_ACTIVE_SUBSCRIPTION_BY_NEXT_PAYMENT,
				 SubscriptionEntity.class)
				.setParameter("nextPayment", DateUtil.calculateLastTimeOfDay())
				.getResultList();
		for (SubscriptionEntity s : subs) {
			try {
				EasypayResponse r = mib3Client.modifyAuthSubscription(Constants.MERCHANT_ID, s.getEasypayAuthId(), "RENEW");
				log.info("Got response code ["+r.getHttpCode()+"] ");
				if (r.getHttpCode() == 400) {
					log.warn("Subscription ["+s+"] could not be renewed, got response from server: "+r.getBody());
					// unmarshall response
					MessagesDomain messages =  MAPPER.readValue(r.getBody(), MessagesDomain.class);
					// Delete Subscription if it never was commited
					if ("TRANSACTION_NOT_COMMITTED".equals(messages.getMessages().get(0).getCode().toString())){
						em.remove(s);
						log.warn("Subscription ["+s+"] was deleted, because it was never commited, and hence dont exists on swisscom side: "+r);
					}
				} else {
					if (s.getService().getDuration() != null && s.getService().getDurationUnit() != null) {
						GregorianCalendar referenceDate = new GregorianCalendar();
						referenceDate.setTime(s.getNextPayment());
						s.setNextPayment(DateUtil.calculateSubscriptionEndDate(referenceDate, 
								s.getService().getDurationUnit(), s.getService().getDuration()));
					}
					log.info("Subscription ["+s+"] is renewed");
				}
			} catch (Exception e) {
				log.error("Subscription ["+s+"] could not be renewed, cause: "+e.getMessage());
			} 
		}
	}
	
}
