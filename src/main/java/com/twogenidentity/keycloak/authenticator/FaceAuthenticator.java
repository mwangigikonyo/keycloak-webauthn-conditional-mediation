package com.twogenidentity.keycloak.authenticator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.WebAuthnPasswordlessAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.TokenManager;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.sessions.AuthenticationSessionModel;

import com.twogenidentity.keycloak.authenticator.model.IdentiYuCustomer;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

public class FaceAuthenticator extends WebAuthnPasswordlessAuthenticator {
	
	
	public FaceAuthenticator(KeycloakSession session) {
		super(session);
	}

	Logger logger = Logger.getLogger(getClass());
	
	@Override
	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
	    logger.info("\n\n\t\t :: configuredFor called ... session=" + session + ", realm=" + realm + ", user=" + user+"\n\n");
	//  return session.userCredentialManager().isConfiguredFor(realm, user, "secret_question");
	    return true;
	}
	
	/*@Override
	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
	   //user.addRequiredAction(“SECRET_QUESTION_REQUIRED_ACTION_PROVIDER_ID”);
		//user.addRequiredAction("SECRET_QUESTION_REQUIRED_ACTION_PROVIDER_ID");
	}*/
	
	@Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.info("authenticate called ... context = " + context);
 
        Response challenge = context.form().createForm("face-validation.ftl");
        logger.info("\n\n\t Challenge: "+ challenge.toString());
        context.challenge(challenge);
    }
	
	
	@Override
    public void action(AuthenticationFlowContext context) {
        logger.info("action called ... context = " + context);
        Response challenge = null;
        Boolean recognised = validateFace(context);
 
        
        if(validateFace(context)) {
            context.success();
        } else {
            challenge = context.form()
                    .setInfo("Hello " + recognised)
                    .createForm("face-validation.ftl");
            context.failureChallenge(AuthenticationFlowError.UNKNOWN_USER, challenge);
        }
 
    }
	
	private String getImageBase64(String imageUrl) {
		try (InputStream inputStream = new URL(imageUrl).openStream()) {
            // Read the image bytes
            byte[] imageBytes = inputStream.readAllBytes();

            // Encode to Base64
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
	}
	
	protected Boolean validateFace(AuthenticationFlowContext context) {
		
		
		try {
			
			
	        IdentiYuCustomer identiyuCustomer = getIdentiYuCustomer(context);
	        
	        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
	        String imageData = formData.getFirst("imageCanvas");
	        logger.info("imageData ==================>" + imageData.substring(0, 50));
	        
	        String emailAddress = formData.getFirst("emailAddress");
	        logger.info("emailAddress ==================>" + emailAddress );
	        imageData = imageData.split(",")[1];
	        
	        String referenceFaceId = createFaceId(getImageBase64(identiyuCustomer.getFace1Url()));
	        String probeFaceId = createFaceId(imageData);
	        int score = probeFaceSimilarityToReferenceFace(referenceFaceId, probeFaceId);
	        logger.info("\n\n\t identiyuCustomer: "+ identiyuCustomer+"\n\t");
	        
	        if(score>=0.4) {
	        	return true;
	        }else if(score<0.4) {
	        	return false;
	        }
	        
	        int i = 100;
			if(i==100) {
				logger.info("\n\n\t Validating face. Faking a true for now.");
				return false;
			}
			
	        
			
	        
	        byte[] decodedImage = null;
	        try {
	            decodedImage = Base64.getDecoder().decode(imageData.getBytes("UTF-8"));
	        } catch (UnsupportedEncodingException e1) {
	            e1.printStackTrace();
	        }
	        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
	             
	             final String subscriptionKey = "<Subscription Key>";
	 
	             final String uriBase =
	                    "https://<My Endpoint String>.com/face/v1.0/detect";
	            logger.info(" Calling Face Id. ");
	            URIBuilder builder = new URIBuilder(uriBase);
	            builder.setParameter("returnFaceId", "true");
	            builder.setParameter("returnFaceLandmarks", "false");
	            builder.setParameter("returnFaceAttributes", "");
	            URI uri = builder.build();
	            HttpPost request = new HttpPost(uri);
	             
	            request.setHeader("Content-Type", "application/octet-stream");
	            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
	             
	            ByteArrayEntity reqEntity = new ByteArrayEntity(decodedImage);
	            request.setEntity(reqEntity);
	 
	            HttpResponse response = client.execute(request);
	            HttpEntity entity = response.getEntity();
	         
	            String faceId = "";
	             
	            if (entity != null)
	            {
	                // Format and display the JSON response.
	                System.out.println("REST Response:\n");
	 
	                String jsonString = EntityUtils.toString(entity).trim();
	                if (jsonString.charAt(0) == '[') {
	                	JsonElement jsonElement = JsonParser.parseString(jsonString);
	                	JsonArray jsonArray = jsonElement.getAsJsonArray();
	                	//JSONArray jsonArray = new JSONArray(jsonString);
	                    System.out.println(jsonArray.toString());
	                    if(jsonArray.size() > 1) {
	                        throw new Error("Multiple faces in the image");
	                    }
	                    faceId = jsonArray.get(0).getAsJsonObject()
	                    		.get("faceId").getAsString();
	                }
	                else if (jsonString.charAt(0) == '{') {
	                	JsonElement jsonElement = JsonParser.parseString(jsonString);
	                	
	                    JsonObject jsonObject = jsonElement.getAsJsonObject();
	                    System.out.println(jsonObject.toString());
	                    faceId = jsonObject.get("faceId").getAsString();
	                } else {
	                    System.out.println(jsonString);
	                }
	            }
	             
	             
	            URIBuilder builderVerify = new URIBuilder("https://westus.api.cognitive.microsoft.com/face/v1.0/verify");
	 
	 
	            URI uriVerify = builderVerify.build();
	            HttpPost requestVerify = new HttpPost(uriVerify);
	            requestVerify.setHeader("Content-Type", "application/json");
	            requestVerify.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
	 
	            String personId = context.getUser().getFirstAttribute("personId");
	            String personGroupId = context.getUser().getFirstAttribute("personGroupId");
	 
	 
	            // Request body
	            StringEntity reqEntityVerify = new StringEntity("{\n" + 
	                    "    \"faceId\": \""+ faceId +"\",\n" + 
	                    "    \"personId\": \""+ personId +"\",\n" + 
	                    "    \"personGroupId\": \""+ personGroupId +"\",\n" + 
	                    "}");
	            request.setEntity(reqEntityVerify);
	 
	            HttpResponse responseVerify = client.execute(request);
	            HttpEntity entityVerify = responseVerify.getEntity();
	 
	            if (entityVerify != null) 
	            {
	                String jsonStringVerify = EntityUtils.toString(entityVerify).trim();
	                JsonElement jsonElement = JsonParser.parseString(jsonStringVerify);
                	
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    
	                JsonObject jsonObjectVerify = jsonObject.getAsJsonObject();
	                return jsonObjectVerify.get("isIdentical").getAsBoolean();
	            }
	 
	        } catch (IOException | URISyntaxException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            return true;
	        }
	        return true;
		}catch(Exception e) {
			logger.error(e);
			return true;
		}
 
    }

	/*@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}*/

	private int probeFaceSimilarityToReferenceFace(String referenceFaceId, String probeFaceId) {
		CloseableHttpClient client = null;
		int score = -1;
		
		try {
			
			client = HttpClientBuilder.create().build();
			
			 
            final String uriBase = "https://id-api.sozuri.net";
           
            logger.info(" Creating Face Id.");
           
            URIBuilder builder = new URIBuilder(uriBase);
            builder.setPath("/api/v1/faces/"+probeFaceId+"/similarity");
            
            URI uri = builder.build();
            String fullUrl = uri.toString();
            logger.info("\n\n\t\t at probeFaceSimilarityToReferenceFace Full URL >>>>>>>: " + fullUrl+"\n\n");
            HttpPost request = new HttpPost(uri);
            JsonObject referenceFace = new JsonObject();
            referenceFace.addProperty("referenceFace", "/api/v1/faces/"+referenceFaceId);
            logger.info(" \n\n\t referenceFace.getAsString() ::: "+referenceFace.getAsString());
	        StringEntity reqEntity = new StringEntity(referenceFace.toString(),ContentType.APPLICATION_JSON);
            request.setEntity(reqEntity);
            
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            
            String jsonString = EntityUtils.toString(entity).trim();
            logger.info("\n\n\t\t Probe face similarity resp >>>>>>> jsonString :: "+jsonString+"\n\n\n\n");
            /*ObjectMapper objectMapper = JsonMapper.builder()
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .build();
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            logger.info(" \n\n\t\t jsonArray>>>>>>>>> "+ jsonArray +"\n\n\n");
            ArrayList<IdentiYuCustomer> identiYuCustomers = objectMapper.readValue(jsonString, new TypeReference<ArrayList<IdentiYuCustomer>>() {});
            
            logger.info("\n\n\n\t IdentiyuCustomers#toString(): "+identiYuCustomers.toString()+" \n\n\n");
			
            identiYuCustomer = identiYuCustomers.get(0);
            
            logger.info("\n\n\n\t IdentiyuCustomer#toString(): "+identiYuCustomers.toString()+" \n\n\n");
			*/
		}catch(Exception e) {
			logger.error(e);
		}finally {
			try {
				if(client!=null) {
					client.close();
				}
			} catch (Exception e) {
			}
		}
		
		return score;
	}

	private String createFaceId(String imageBase64) {
		
		CloseableHttpClient client = null;
		String ref = "";
		
		try {
			
			client = HttpClientBuilder.create().build();
			
			final String identiYuSubscriptionKey = "<Subscription Key>";
			 
            final String uriBase = "https://id-api.sozuri.net";
           
            logger.info(" Creating Face Id.");
           
            URIBuilder builder = new URIBuilder(uriBase);
            builder.setPath("/api/v1/faces");
            
            URI uri = builder.build();
            String fullUrl = uri.toString();
            logger.info("\n\n\t\t at createFaceId Full URL >>>>>>>: " + fullUrl+"\n\n");
            HttpPost request = new HttpPost(uri);
            
            byte[] decodedImage = null;
	        try {
	            decodedImage = Base64.getDecoder().decode(imageBase64.getBytes("UTF-8"));
	        } catch (UnsupportedEncodingException e1) {
	            e1.printStackTrace();
	        }
	        JsonObject image = new JsonObject();
	        image.addProperty("data", imageBase64);
	        
	        
	        com.google.gson.JsonObject detection = new JsonObject();
	        detection.addProperty("mode", "STRICT");
	        
	        JsonObject faceSizeRatio = new JsonObject();
	        faceSizeRatio.addProperty("min", 0.05);
	        faceSizeRatio.addProperty("min", 0.5);
	        detection.add("faceSizeRatio", faceSizeRatio);
	        
	        
	        
	        JsonObject jsob = new JsonObject();
	        jsob.add("image", image);
	        jsob.add("detection", detection);
	        
	        
	        logger.info("\n\n\t\t payload :: jsob.getAsString() :::: "+jsob.toString()+"\n\n\n\n");
             
	        StringEntity reqEntity = new StringEntity(jsob.toString());
            request.setEntity(reqEntity);
            
            request.setHeader("IdentiYu-Subscription-Key", identiYuSubscriptionKey);
            request.setHeader("Content-Type", "application/json");
            
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            
            String jsonString = EntityUtils.toString(entity).trim();
            logger.info("\n\n\t\t create Face resp >>>>>>> jsonString :: "+jsonString+"\n\n\n\n");
            /*ObjectMapper objectMapper = JsonMapper.builder()
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .build();
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            logger.info(" \n\n\t\t jsonArray>>>>>>>>> "+ jsonArray +"\n\n\n");
            ArrayList<IdentiYuCustomer> identiYuCustomers = objectMapper.readValue(jsonString, new TypeReference<ArrayList<IdentiYuCustomer>>() {});
            
            logger.info("\n\n\n\t IdentiyuCustomers#toString(): "+identiYuCustomers.toString()+" \n\n\n");
			
            identiYuCustomer = identiYuCustomers.get(0);
            
            logger.info("\n\n\n\t IdentiyuCustomer#toString(): "+identiYuCustomers.toString()+" \n\n\n");
			*/
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}finally {
			try {
				if(client!=null) {
					client.close();
				}
			} catch (Exception e) {
			}
		}
		
		return ref;
	}

	private IdentiYuCustomer getIdentiYuCustomer(AuthenticationFlowContext context) {
		IdentiYuCustomer identiYuCustomer = null;
		CloseableHttpClient client = null;
		try {
			
			UserModel user = context.getUser();//.getFirstAttribute("face_recognition_name");
	        String userEmail = user.getEmail();
			logger.info("\n\n\t userEmail : "+ userEmail +"\n\t");
			
			client = HttpClientBuilder.create().build();
			
			final String identiYuSubscriptionKey = "<Subscription Key>";
			 
            final String uriBase = "http://localhost:8443";
           
            logger.info(" Calling Face Id. ");
           
            URIBuilder builder = new URIBuilder(uriBase);
            ///apiv1/customer?emailAddress=mwangi.gikonyo.t@gmail.com&sort=cretionTime%20DESC&limit=1
            builder.setPath("/apiv1/customer");
            builder.setParameter("emailAddress", userEmail);
            builder.setParameter("sort", "creationTime DESC");
            builder.setParameter("limit", "1");
            builder.setParameter("isDeleted", "false");
           
            URI uri = builder.build();
            String fullUrl = uri.toString();
            logger.info("\n\n\t\t Full URL >>>>>>>: " + fullUrl+"\n\n");
            HttpGet request = new HttpGet(uri);
            
            request.setHeader("IdentiYu-Subscription-Key", identiYuSubscriptionKey);
            request.setHeader("IdentiYu-Subscription-Key", identiYuSubscriptionKey);
            request.setHeader("Content-Type", "application/json");
            
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            
            String jsonString = EntityUtils.toString(entity).trim();
            logger.info("\n\n\t\t get IdentiYu User resp >>>>>>> jsonString :: "+jsonString+"\n\n\n\n");
            ObjectMapper objectMapper = JsonMapper.builder()
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .build();
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            logger.info(" \n\n\t\t jsonArray>>>>>>>>> "+ jsonArray +"\n\n\n");
            ArrayList<IdentiYuCustomer> identiYuCustomers = objectMapper.readValue(jsonString, new TypeReference<ArrayList<IdentiYuCustomer>>() {});
            
            logger.info("\n\n\n\t IdentiyuCustomers#toString(): "+identiYuCustomers.toString()+" \n\n\n");
			
            identiYuCustomer = identiYuCustomers.get(0);
            
            logger.info("\n\n\n\t IdentiyuCustomer#toString(): "+identiYuCustomers.toString()+" \n\n\n");
			
		}catch(Exception e) {
			logger.error(e);
		}finally {
			try {
				if(client!=null) {
					client.close();
				}
			} catch (Exception e) {
			}
		}
		return identiYuCustomer;
	}

	@Override
	public boolean requiresUser() {
		// TODO Auto-generated method stub
		return true;
	}

}
