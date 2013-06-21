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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Named;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@Named
public class FacesUtil {
	
	public String formatDate(Date date){
		return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date);
	}


}
