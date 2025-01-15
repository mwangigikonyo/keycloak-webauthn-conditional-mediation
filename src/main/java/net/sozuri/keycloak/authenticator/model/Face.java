package net.sozuri.keycloak.authenticator.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Face  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -179347629680391379L;
	
	private Long createdAt;
	private Long updatedAt;
    private int id;
    private String dot_id;
    private Links links;
    private String creationTime;
    private int customer;
	public Long getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}
	public Long getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Long updatedAt) {
		this.updatedAt = updatedAt;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDot_id() {
		return dot_id;
	}
	public void setDot_id(String dot_id) {
		this.dot_id = dot_id;
	}
	public Links getLinks() {
		return links;
	}
	public void setLinks(Links links) {
		this.links = links;
	}
	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	public int getCustomer() {
		return customer;
	}
	public void setCustomer(int customer) {
		this.customer = customer;
	}
	@Override
	public String toString() {
		return "Face [createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", id=" + id + ", dot_id=" + dot_id
				+ ", links=" + links + ", creationTime=" + creationTime + ", customer=" + customer + "]";
	}
    

}
