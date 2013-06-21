/*
 (c) Copyright Swisscom (Schweiz) AG. All rights reserved.

 This product is the proprietary and sole property of Swisscom (Schweiz) AG
 Use, duplication or dissemination is subject to prior written consent of
 Swisscom (Schweiz) AG.

 $Id: $

 */
package com.swisscom.refimpl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 
 * @author $Author: tgdscald$
 * @version $Revision: $ / $Date: 05.01.2012$
 */
/**
 * The class MessagesDomain.java.
 * 
 * @author TGDSCALD
 */
public class MessagesDomain implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private List<MessageDomain> messages;

	public MessagesDomain() {
		super();
	}

	/**
	 * @param messages
	 */
	public MessagesDomain(List<MessageDomain> messages) {
		super();
		this.messages = messages;
	}



	public List<MessageDomain> getMessages() {
		if (messages == null)
			messages = new ArrayList<MessageDomain>();
		return messages;
	}

	public void setMessages(List<MessageDomain> messages) {
		this.messages = messages;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessagesDomain [messages=" + messages + "]";
	}

}
