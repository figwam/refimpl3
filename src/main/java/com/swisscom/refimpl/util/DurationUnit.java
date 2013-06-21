/*
 (c) Copyright Swisscom (Schweiz) AG. All rights reserved.

 This product is the proprietary and sole property of Swisscom (Schweiz) AG
 Use, duplication or dissemination is subject to prior written consent of
 Swisscom (Schweiz) AG.

 $Id: $

 */
package com.swisscom.refimpl.util;



/**
 * @author $Author: tgdscald$
 * @version $Revision: $ / $Date: 18.05.2009$
 */
public enum DurationUnit {
	DAY("Day"), MONTH("Month"), WEEK("Week"), EVENT("Event");
	String value;

	private DurationUnit(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
