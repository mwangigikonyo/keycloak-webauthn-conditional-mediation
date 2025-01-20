package net.sozuri.keycloak.authenticator.model;

import java.io.Serializable;

public class FaceValidateResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5715584355241504529L;
	
	private Boolean faceIsValid;
	private double similarity;
	public Boolean getFaceIsValid() {
		return faceIsValid;
	}
	public void setFaceIsValid(Boolean faceIsValid) {
		this.faceIsValid = faceIsValid;
	}
	public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	@Override
	public String toString() {
		return "FaceValidateResponse [faceIsValid=" + faceIsValid + ", similarity=" + similarity + "]";
	}
	
	

}
