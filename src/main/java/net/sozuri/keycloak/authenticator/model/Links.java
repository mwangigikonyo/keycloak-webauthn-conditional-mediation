package net.sozuri.keycloak.authenticator.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Links implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6902159876277321279L;
	
	private String self;
	
	public String getSelf() {
		return self;
	}
	
	public void setSelf(String self) {
		this.self = self;
	}
	
	@Override
	public String toString() {
		return "Links [self=" + self + "]";
	}
	
	

}
