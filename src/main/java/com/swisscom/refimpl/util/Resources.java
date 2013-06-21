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
package com.swisscom.refimpl.util;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class Resources {
	

	@Produces
	public Logger getLog(InjectionPoint injectionPoint) {
		return LoggerFactory.getLogger(injectionPoint.getMember()
				.getDeclaringClass());
	}
	
	@Produces
    @ResourceBundleQualifier("messages")
	public ResourceBundle getMessagesResourceBundle(InjectionPoint injectionPoint) {
		String bundlename = "com.swisscom.ce.checkout.messages";
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		return ResourceBundle.getBundle(bundlename, locale);
	}
	
	@SuppressWarnings("unused")
	@PersistenceContext(name="Refimpl3", unitName="Refimpl3")
	@Produces
	private EntityManager em;
	
}
