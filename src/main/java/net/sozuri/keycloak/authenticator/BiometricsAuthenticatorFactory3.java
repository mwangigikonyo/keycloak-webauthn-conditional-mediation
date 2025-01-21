package net.sozuri.keycloak.authenticator;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.WebAuthnAuthenticatorFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.credential.WebAuthnCredentialModel;

public class BiometricsAuthenticatorFactory3 extends WebAuthnAuthenticatorFactory {

	public static final String PROVIDER_ID = "identiyu-biometrics-webauthn-Beta3";

    @Override
    public String getReferenceCategory() {
        return WebAuthnCredentialModel.TYPE_PASSWORDLESS;
    }

    @Override
    public String getDisplayType() {
        return "IdentiYu - WebAuthn Biometrics-Beta3";
    }

    @Override
    public String getHelpText() {
        return "IdentiYu - WebAuthn with Biometrics-Beta3";
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new BiometricsAuthenticator3(session);
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
