/*
 (c) Copyright Swisscom (Schweiz) AG. All rights reserved.

 This product is the proprietary and sole property of Swisscom (Schweiz) AG
 Use, duplication or dissemination is subject to prior written consent of
 Swisscom (Schweiz) AG.

 $Id: $

 */
package com.swisscom.refimpl.util;

import java.util.Date;
import java.util.GregorianCalendar;



/**
 * @author $Author: tgdscald$
 * @version $Revision: $ / $Date: 18.05.2009$
 */
public class DateUtil {
	
	public static Date calculateSubscriptionEndDate(GregorianCalendar date, DurationUnit durationUnit, Integer duration) {
		if (durationUnit.equals(DurationUnit.DAY)){
			date.add(GregorianCalendar.HOUR, duration*24);
		} else if (durationUnit.equals(DurationUnit.WEEK)){
			date.add(GregorianCalendar.HOUR, duration*7*24);
		} else if (durationUnit.equals(DurationUnit.MONTH)){
			date.add(GregorianCalendar.MONTH, duration);
		} 
		return date.getTime();
	}
	


	public static Date calculateLastTimeOfDay() {
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(GregorianCalendar.HOUR_OF_DAY, 23);
		gc.set(GregorianCalendar.MINUTE, 59);
		gc.set(GregorianCalendar.SECOND, 59);
		gc.set(GregorianCalendar.MILLISECOND, 999);
		return gc.getTime();
	}
	
}
