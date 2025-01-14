package com.twogenidentity.keycloak.authenticator.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentiYuCustomer implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7203567529542772242L;
	private String dot_id;
	private String creationTime;
	private List<Face> faces;
	private List<Document> documents;
    private List<Selfie> selfies;
	private String emailAddress;
	private String phoneNumber;
	private Links links;
	private String face1Url;
	private String face2Url;
	
	public String getDot_id() {
		return dot_id;
	}
	public void setDot_id(String dot_id) {
		this.dot_id = dot_id;
	}
	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	public List<Face> getFaces() {
		return faces;
	}
	public void setFaces(List<Face> faces) {
		this.faces = faces;
	}
	public List<Document> getDocuments() {
		return documents;
	}
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	public List<Selfie> getSelfies() {
		return selfies;
	}
	public void setSelfies(List<Selfie> selfies) {
		this.selfies = selfies;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Links getLinks() {
		return links;
	}
	public void setLinks(Links links) {
		this.links = links;
	}
	public String getFace1Url() {
		return face1Url;
	}
	public void setFace1Url(String face1Url) {
		this.face1Url = face1Url;
	}
	public String getFace2Url() {
		return face2Url;
	}
	public void setFace2Url(String face2Url) {
		this.face2Url = face2Url;
	}
	@Override
	public String toString() {
		return "IdentiYuCustomer [dot_id=" + dot_id + ", creationTime=" + creationTime + ", faces=" + faces
				+ ", documents=" + documents + ", selfies=" + selfies + ", emailAddress=" + emailAddress
				+ ", phoneNumber=" + phoneNumber + ", links=" + links + ", face1Url=" + face1Url + ", face2Url="
				+ face2Url + "]";
	}
	
}
