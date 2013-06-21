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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.swisscom.refimpl.entity.EventPurchaseEntity;
import com.swisscom.refimpl.entity.SubscriptionEntity;
import com.swisscom.refimpl.entity.UserEntity;
import com.swisscom.refimpl.events.ServiceChangedEvent;
import com.swisscom.refimpl.events.ServiceChangedEvent.Operation;
import com.swisscom.refimpl.model.Service;
import com.swisscom.refimpl.model.User;
import com.swisscom.refimpl.util.ResourceBundleQualifier;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@SessionScoped
@Named
public class Login implements Serializable {

	@Inject
	transient Logger log;

    @Inject
    transient ServiceControl serviceControl;

    @Inject
    transient UserManager userManager;
    
	@Inject
	@ResourceBundleQualifier("messages")
	transient ResourceBundle bundle;
    
	String username;
	String password;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private User currentUser;
	
//	@PostConstruct
//	public void init() {
//		msisdn = "079xxxxxxx";
//		login();
//	}
	
	public void register() {
		userManager.registerUser(username, password);
	}
	
	public void delete() {
		userManager.deleteUser(username);
	}
	
	public String login() {
		log.info("Login received with username: "+username+", password: '*****'");
		
		UserEntity user = userManager.retrieveUser(username, password); // this object is persisted! we have to detach it
		if (user == null) {
			log.info("Login with username: "+username+", password: '*****' failed.");
			FacesContext.getCurrentInstance().addMessage("loginMsg", new FacesMessage("Invalid Username/Password combination"));
			return null;
		} else {
			currentUser = new User();
			currentUser.setUsername(username);
			currentUser.setExtId(user.getExtUserId());
			List<Service> purchasedServices = new ArrayList<Service>();
			List<Service> purchasedEvents = new ArrayList<Service>();
			Date refDate = new Date();
			for (SubscriptionEntity s : user.getSubscriptions()) {
				log.info("Found auth subscription for user "+username+": "+s.getEasypayAuthId());
				Service serviceToAdd = serviceControl.entityToDomain(s.getService());
				
				// set some parameters, which are only on subscription entity
				serviceToAdd.setEasypayAuthId(s.getEasypayAuthId());
				serviceToAdd.setCancelledOn(s.getCancelledOn());
				serviceToAdd.setCreatedOn(s.getCreatedOn());
				
				// do filter: add only NOT canceled OR cancelled BUT NOT expired services
				if (serviceToAdd.getValidTill() == null 
						|| serviceToAdd.getValidTill().after(refDate)) {
					purchasedServices.add(serviceToAdd);
				}
			}
			for (EventPurchaseEntity e : user.getEvents()) {
				Service serviceToAdd = serviceControl.entityToDomain(e.getService());
				
				serviceToAdd.setTransactionId(e.getTransactionId());
				serviceToAdd.setCreatedOn(e.getCreatedOn());
				//serviceToAdd.setExtId(e.getExtEventPurchasesId());
				purchasedEvents.add(serviceToAdd);
			}
			currentUser.setServices(purchasedServices);
			currentUser.setPurchasedEvents(purchasedEvents);
			List<Service> availableServices = new ArrayList<Service>();
			
			// filter user purchased services in all services
			for (Service s : serviceControl.getServices()) {
				if (!purchasedServices.contains(s)) {
					availableServices.add(s);
				}
			}
			currentUser.setAvailableServices(availableServices);
			log.info("Login with username: "+username+", passwordHash: '"+user.getPasswordHash()+"' was successful.");
			
			/*
			 * http://www.mkyong.com/jsf2/implicit-navigation-in-jsf-2-0/
			 * 
			 * By default, JSF 2 is perform a forward while navigating to another page, it caused the page URL is always one 
			 * behind :). For example, when you move from “page1.xhtml” to “page2.xhtml”, the browser URL address bar will 
			 * still showing the same “page1.xhtml” URL.
			 * 
			 * To avoid this, you can tell JSF to use the redirection by append the “faces-redirect=true” to the end 
			 * of the “outcome” string.
			 * 
			 * */
			
			return "home";
		}
	}

	public void logout() {
		String user = currentUser.getUsername();
		currentUser = null;
		log.info("Logout for user: "+user+" was successful.");
	}

	public boolean isLoggedIn() {
		return currentUser != null;
	}

	@Produces
	@LoggedIn
	public User getCurrentUser() {
		return currentUser;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean updateUserSelectedServiceSubs(String easypayAuthId) {
		if (easypayAuthId != null) {
			for (Service s : currentUser.getServices()) {
				if (s.getEasypayAuthId().equals(easypayAuthId)) {
					currentUser.setSelectedService(s);
					break;
				}
			}
		} else {
			// this can only happen, if user tries to manipulate the HTTP GET parametes
			// so just ignore
			log.info("easypayAuthId evaluates to null");
		}
		return currentUser.getSelectedService() != null;
	}
	
	public boolean updateUserSelectedServiceEvent(String transactionId) {
		if (transactionId != null) {
			for (Service s : currentUser.getPurchasedEvents()) {
				if (s.getTransactionId().equals(transactionId)) {
					currentUser.setSelectedService(s);
					break;
				}
			}
		} else {
			// this can only happen, if user tries to manipulate the HTTP GET parametes
			// so just ignore
			log.info("easypayAuthId evaluates to null");
		}
		return currentUser.getSelectedService() != null;
	}
	
	public boolean updateUserSelectedAvailableService(String extId) {
		if (extId != null) {
			currentUser.setSelectedAvailableService(serviceControl.retrieveServiceByExtId(extId));
		} else {
			// this can only happen, if user tries to manipulate the HTTP GET parametes
			// so just ignore
			log.info("extId evaluates to null");
		}
		return currentUser.getSelectedAvailableService() != null;
	}
	
	public void listenToServicesChanged(@Observes ServiceChangedEvent serviceEvent){
		if (serviceEvent.getOperation().equals(Operation.ADD_AVAILABLE_FOR_PURCHASE)) {
			log.debug("Add service to users 'offered services list'");
			currentUser.getAvailableServices().add(serviceEvent.getService());
		} else if (serviceEvent.getOperation().equals(Operation.ADD_USER_SUBSCRIPTION_PURCHASED)) {
			log.debug("Add service to user purchased");
			currentUser.getServices().add(serviceEvent.getService());
			currentUser.getAvailableServices().remove(serviceEvent.getService());
		} else if (serviceEvent.getOperation().equals(Operation.CANCEL_USER_SUBSCRIPTION_PURCHASED)) {
			log.debug("cancel user purchased");
			Date cancelledOn = new Date();
			serviceControl.cancelUserSubscriptionByAuthId(serviceEvent.getService().getEasypayAuthId(), cancelledOn);
			
			// get the full service, and set the 'cancelledOn' it. The service in event-fire contains only easypayAuthId and serviceExtId
			int idx = currentUser.getServices().indexOf(serviceEvent.getService());
			currentUser.getServices().get(idx).setCancelledOn(cancelledOn);
			
		} else if (serviceEvent.getOperation().equals(Operation.ADD_USER_EVENT_PURCHASED)) {
			log.debug("event service purchased");
			currentUser.getPurchasedEvents().add(serviceEvent.getService());
		} else if (serviceEvent.getOperation().equals(Operation.REMOVE_USER_PURCHASED)) {
			log.debug("delete user purchased");
			serviceControl.deleteUserSubscriptionByAuthId(serviceEvent.getService().getEasypayAuthId());
			
			// get the full service. The service in event-fire contains only easypayAuthId and serviceExtId
			int idx = currentUser.getServices().indexOf(serviceEvent.getService());
			Service s = currentUser.getServices().get(idx);
			currentUser.getServices().remove(s);
			currentUser.getAvailableServices().add(s);
			
		}else if (serviceEvent.getOperation().equals(Operation.SERVICE_DELETED)) {
			log.debug("delete service from user list");
			// get the full service. The service in event-fire contains only easypayAuthId and serviceExtId
			
			// remove the service of user either from services list (purchased) or availableServices (not purchased) or events
			List<Service> servicesToRemove = new ArrayList<Service>();
			for(Service s : currentUser.getServices()){
				if (s.equals(serviceEvent.getService())){
					servicesToRemove.add(s);
				}
			}
			currentUser.getServices().removeAll(servicesToRemove);
			
			servicesToRemove.clear();
			for(Service s : currentUser.getPurchasedEvents()){
				if (s.equals(serviceEvent.getService())){
					servicesToRemove.add(s);
				}
			}
			currentUser.getPurchasedEvents().removeAll(servicesToRemove);
			
			servicesToRemove.clear();
			for(Service s : currentUser.getAvailableServices()){
				if (s.equals(serviceEvent.getService())){
					servicesToRemove.add(s);
				}
			}
			currentUser.getAvailableServices().removeAll(servicesToRemove);
		}
    }
	
	public String buildHumanReadableDuration(Service s){
		if (s.isEvent()) {
			return bundle.getString("without.abonnement");
		} else {
			String per = "per";
			String dur = "NONE";
			if (s.getDuration() > 1) {
				// singular case
				per = bundle.getString("word.for");
				dur = bundle.getString("word."+s.getDurationUnit().toString().toLowerCase()+".plural");
				return per+" "+s.getDuration()+" "+dur;
			} else {
				// plural case
				per = bundle.getString("word.per");
				dur = bundle.getString("word."+s.getDurationUnit().toString().toLowerCase());
				return per+" "+dur;
			}
		}
	}


	/**
	 * @param bundle the bundle to set
	 */
	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}
	
}