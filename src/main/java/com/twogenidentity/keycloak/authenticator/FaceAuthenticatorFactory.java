package com.twogenidentity.keycloak.authenticator;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.WebAuthnAuthenticator;
import org.keycloak.authentication.authenticators.browser.WebAuthnAuthenticatorFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.credential.WebAuthnCredentialModel;

public class FaceAuthenticatorFactory extends WebAuthnAuthenticatorFactory {

	public static final String PROVIDER_ID = "identiyu-webauthn-authenticator";

    @Override
    public String getReferenceCategory() {
        return WebAuthnCredentialModel.TYPE_PASSWORDLESS;
    }

    @Override
    public String getDisplayType() {
        return "IdentiYu - WebAuthn Passwordless Conditional Mediator Authenticator";
    }

    @Override
    public String getHelpText() {
        return "IdentiYu - Authenticator for Passwordless WebAuthn authentication that support Passkeys Form-fill";
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new FaceAuthenticator(session);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

}
