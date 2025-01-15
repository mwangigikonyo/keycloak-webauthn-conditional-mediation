package net.sozuri.keycloak.authenticator;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.WebAuthnAuthenticatorFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.credential.WebAuthnCredentialModel;

public class BiometricsAuthenticatorFactory extends WebAuthnAuthenticatorFactory {

	public static final String PROVIDER_ID = "identiyu-biometrics-webauthn-authenticator-Beta1";

    @Override
    public String getReferenceCategory() {
        return WebAuthnCredentialModel.TYPE_PASSWORDLESS;
    }

    @Override
    public String getDisplayType() {
        return "IdentiYu - WebAuthn Biometrics Authenticator-Beta1";
    }

    @Override
    public String getHelpText() {
        return "IdentiYu - Authenticator for Passwordless WebAuthn authentication that support Biometrics Form-fill-Beta1";
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new BiometricsAuthenticator(session);
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
