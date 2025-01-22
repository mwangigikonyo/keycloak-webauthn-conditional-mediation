package net.sozuri.keycloak.authenticator;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.WebAuthnPasswordlessAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.KeycloakSessionTask;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import net.sozuri.keycloak.authenticator.model.FaceValidateResponse;
import net.sozuri.keycloak.authenticator.model.IdentiYuCustomer;

public class BiometricsAuthenticator5 extends WebAuthnPasswordlessAuthenticator {
	
	private final double FACE_MATCH_THRESHHOLD = 0.4;
	
	public BiometricsAuthenticator5(KeycloakSession session) {
		super(session);
	}

	Logger logger = Logger.getLogger(getClass());
	
	@Override
	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
	    //logger.info("\n\n\t\t :: configuredFor called ... session=" + session + ", realm=" + realm + ", user.getFirstName()=" + user.getFirstName() +"\n\n");
	    //return session.userCredentialManager().isConfiguredFor(realm, user, "secret_question");
	    return true;
	}
	
	@Override
	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
	   //user.addRequiredAction(“SECRET_QUESTION_REQUIRED_ACTION_PROVIDER_ID”);
		//user.addRequiredAction("SECRET_QUESTION_REQUIRED_ACTION_PROVIDER_ID");
	}
	
	@Override
    public void authenticate(AuthenticationFlowContext context) {
		
		///logger.info("authenticate called ... context = " + context);
		
		int maxRetries = 3; // Set your desired maximum retries
        int retryIntervalMillis = 1000; // Set your desired retry interval in milliseconds

        KeycloakSession session = context.getSession();
        KeycloakSessionFactory sessionFactory = session.getKeycloakSessionFactory();
        int transactionTimeoutInSeconds = 120; // Set your desired timeout duration

        KeycloakSessionTask task = new KeycloakSessionTask() {
            @Override
            public void run(KeycloakSession taskSession) {
                // TODO here we fetch the plugin's configurations.
            	// This will most likely be a vault config
            	//logger.info( "Get Identiyu Configurations via Vault here...");
            }
        };
        
        
        try {
        	
        	context.getAuthenticationSession().setAuthNote("identiyuCustomerId", "-1");
    		context.getAuthenticationSession().setAuthNote("biometricsFound", "false");
        	KeycloakModelUtils.runJobInTransactionWithTimeout(sessionFactory, task, transactionTimeoutInSeconds);
        	
        	Response challenge = context.form()
    	            .setAttribute("identityCustomerId", "deprecated")
    	            .createForm("face-validation5.ftl");
            
        	context.challenge(challenge);
        	
        } catch (Exception e) {
        
        	context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred").build());
        
        }
        
    }
	
	
	@Override
    public void action(AuthenticationFlowContext context) {
        Response challenge = null;
        FaceValidateResponse response = validateFace(context);
        
        if(response.getFaceIsValid()) {
        	context.success();
        } else {
        	challenge = context.form()
        			.setError("Face Match: " + (response.getSimilarity()*100)+"%, "+(response.getFaceIsValid()?"Biometrics check passed. Logging in":"Match failed. Minimum face match: "+(FACE_MATCH_THRESHHOLD*100)+" %"), response)
                    .createForm("face-validation5.ftl");
            context.failureChallenge(AuthenticationFlowError.UNKNOWN_USER, challenge);
        }
 
    }
	
	private String getImageBase64(String imageUrl) {
		InputStream inputStream = null;
		try {
			inputStream = new URL(imageUrl).openStream();
            // Read the image bytes
            byte[] imageBytes = inputStream.readAllBytes();

            // Encode to Base64
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }finally {
        	try {
        		if(inputStream!=null)
        			inputStream.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
        }
		
	}
	
	protected FaceValidateResponse validateFace(AuthenticationFlowContext context) {
		
		FaceValidateResponse response = new FaceValidateResponse();
		
		try {
			
			
	        IdentiYuCustomer identiyuCustomer = getIdentiYuCustomer(context);
	        
	        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
	        String imageData = formData.getFirst("imageCanvas");
	        
	        imageData = (!imageData.isEmpty()||imageData!=null)?imageData.split(",")[1]:null;
	        
	        if(imageData.isEmpty() || imageData==null)
	        	throw new Exception("This authenticator expects a base64 string to "
	        			+ "be passed via the form parameter with the name 'imageCanvas'. "
	        			+ "However this value seems to be null Actual value["+formData.getFirst("imageCanvas")+"]");
	        
	        String referenceFaceId = createFaceId(getImageBase64(identiyuCustomer.getFace1Url()));
	        String probeFaceId = createFaceId(imageData);
	        double score = probeFaceSimilarityToReferenceFace(referenceFaceId, probeFaceId);
	        
	        response.setSimilarity(score);
	        response.setFaceIsValid(score>=FACE_MATCH_THRESHHOLD);
			
	        
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return response;
 
    }

	@Override
	public void close() {
		super.close();
		
	}

	private double probeFaceSimilarityToReferenceFace(String referenceFaceId, String probeFaceId) {
		CloseableHttpClient client = null;
		double score = -1.0;
		
		try {
			
			client = HttpClientBuilder.create().build();
			
			 
            final String uriBase = "https://id-api.sozuri.net";
           
            URIBuilder builder = new URIBuilder(uriBase);
            builder.setPath("/api/v1/faces/"+probeFaceId+"/similarity");
            
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            JsonObject referenceFace = new JsonObject();
            referenceFace.addProperty("referenceFace", "/api/v1/faces/"+referenceFaceId);
            StringEntity reqEntity = new StringEntity(referenceFace.toString());
            
	        request.setEntity(reqEntity);
	        request.setHeader("Content-Type", "application/json");
            
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            
            String jsonString = EntityUtils.toString(entity).trim();
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            JsonObject responseJson = jsonElement.getAsJsonObject();
            
            score = responseJson.get("score").getAsDouble();	
            
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
		
		return score;
	}

	private String createFaceId(String imageBase64) {
		
		CloseableHttpClient client = null;
		String ref = "";
		
		try {
			
			client = HttpClientBuilder.create().build();
			
			final String identiYuSubscriptionKey = "<Subscription Key>";
			 
            final String uriBase = "https://id-api.sozuri.net";
           
            URIBuilder builder = new URIBuilder(uriBase);
            builder.setPath("/api/v1/faces");
            
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            
            JsonObject image = new JsonObject();
	        image.addProperty("data", imageBase64);
	        
	        
	        JsonObject detection = new JsonObject();
	        detection.addProperty("mode", "STRICT");
	        
	        JsonObject faceSizeRatio = new JsonObject();
	        faceSizeRatio.addProperty("min", 0.05);
	        faceSizeRatio.addProperty("max", 0.5);
	        detection.add("faceSizeRatio", faceSizeRatio);
	        	        	        
	        JsonObject jsob = new JsonObject();
	        jsob.add("image", image);
	        jsob.add("detection", detection);
	        
	        
	        StringEntity reqEntity = new StringEntity(jsob.toString());
            request.setEntity(reqEntity);
            
            request.setHeader("IdentiYu-Subscription-Key", identiYuSubscriptionKey);
            request.setHeader("Content-Type", "application/json");
            
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            
            String jsonString = EntityUtils.toString(entity).trim();
            JsonElement jsonElement = JsonParser.parseString(jsonString);
        	
            JsonObject responseJson = jsonElement.getAsJsonObject();
            
            ref = responseJson.get("id").getAsString();
		
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
			
			client = HttpClientBuilder.create().build();
			
			final String identiYuSubscriptionKey = "<Subscription Key>";
			 
            final String uriBase = "https://id-api.sozuri.net";
           
            URIBuilder builder = new URIBuilder(uriBase);
            builder.setPath("/apiv1/customer");
            builder.setParameter("emailAddress", userEmail);
            builder.setParameter("sort", "creationTime DESC");
            builder.setParameter("limit", "1");
            builder.setParameter("isDeleted", "false");
           
            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            
            request.setHeader("IdentiYu-Subscription-Key", identiYuSubscriptionKey);
            request.setHeader("IdentiYu-Subscription-Key", identiYuSubscriptionKey);
            request.setHeader("Content-Type", "application/json");
            
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            
            String jsonString = EntityUtils.toString(entity).trim();
            
            ObjectMapper objectMapper = JsonMapper.builder()
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .build();
            
            ArrayList<IdentiYuCustomer> identiYuCustomers = objectMapper.readValue(jsonString, new TypeReference<ArrayList<IdentiYuCustomer>>() {});
            
            identiYuCustomer = identiYuCustomers.get(0);
            
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
		return true;
	}

}
