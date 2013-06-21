/**
 * 
 */
package com.swisscom.refimpl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5650918101242189614L;
	
	String username;
	
	
	List<Service> services = new ArrayList<Service>();
	List<Service> availableServices = new ArrayList<Service>();
	List<Service> purchasedEvents = new ArrayList<Service>();

	String extId;

	private Service selectedService;
	private Service selectedAvailableService;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public List<Service> getAvailableServices() {
		return availableServices;
	}

	public void setAvailableServices(List<Service> myServices) {
		this.availableServices = myServices;
	}
	
	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	/**
	 * @return the selectedService
	 */
	public Service getSelectedService() {
		return selectedService;
	}

	/**
	 * @param selectedService the selectedService to set
	 */
	public void setSelectedService(Service selectedService) {
		this.selectedService = selectedService;
	}

	/**
	 * @return the selectedAvailableService
	 */
	public Service getSelectedAvailableService() {
		return selectedAvailableService;
	}

	/**
	 * @param selectedAvailableService the selectedAvailableService to set
	 */
	public void setSelectedAvailableService(Service selectedAvailableService) {
		this.selectedAvailableService = selectedAvailableService;
	}
	
	/**
	 * @return the purchasedEvents
	 */
	public List<Service> getPurchasedEvents() {
		Collections.sort(purchasedEvents, Service.COMPARATOR_BY_CREATED_ON);
		return purchasedEvents;
	}

	/**
	 * @param purchasedEvents the purchasedEvents to set
	 */
	public void setPurchasedEvents(List<Service> purchasedEvents) {
		this.purchasedEvents = purchasedEvents;
	}
	
	
}
