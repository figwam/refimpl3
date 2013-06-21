/**
 * 
 */
package com.swisscom.refimpl.events;

import com.swisscom.refimpl.model.Service;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class ServiceChangedEvent {
	
	public enum Operation {
		ADD_AVAILABLE_FOR_PURCHASE, 
		ADD_USER_SUBSCRIPTION_PURCHASED, 
		ADD_USER_EVENT_PURCHASED, 
		CANCEL_USER_SUBSCRIPTION_PURCHASED, 
		REMOVE_USER_PURCHASED, 
		REMOVE_AVAILABLE_FOR_PURCHASE, 
		SERVICE_DELETED;
	}
	
	
	
	public ServiceChangedEvent(Operation operation, Service service) {
		super();
		this.operation = operation;
		this.service = service;
	}
	
	Operation operation;
	Service service;
	
	public Operation getOperation() {
		return operation;
	}
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}

	
}
