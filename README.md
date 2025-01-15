# Keycloak: Webauthn Conditional Meditation Authenticator - With Biometrics Implementation

This is a webauthn authenticator that supports [WebAuth Conditional UI](https://github.com/w3c/webauthn/wiki/Explainer:-WebAuthn-Conditional-UI) or Passkey autofill. It's based on the OOTB [WebAuthnPasswordlessAuthenticator](https://github.com/keycloak/keycloak/blob/main/services/src/main/java/org/keycloak/authentication/authenticators/browser/WebAuthnPasswordlessAuthenticator.java), with a touch of customization and love ♥️ on the [UI](src/main/resources/theme-resources/templates/webauthn-conditional-mediation-authenticate.ftl) side.

When moving from passwords to passkeys, there might be challenges. User experience matters a lot. The default “modal” experience might not be good in some cases. But using the passkey with autofill (WebAuth Conditional UI) feature can improve the login process.  Keycloak version 22 lacks this feature, which is why I developed this custom SPI. Furthermore, it is beneficial to have the flexibility to customize the user experience according to your business requirements.

This custom authenticator is utilized in the following workshop:

- https://github.com/embesozzi/keycloak-workshop-stepup-mfa-biometrics

You will find there a cool workshop covering MFA, passkeys, and step-up authentication.

## Copy gson-2.10.1-sources.jar to <KEYCLOAK_HOME>/lib/lib/deployment

cp /Users/mwangigikoyo/.m2/repository/com/google/code/gson/gson/2.10.1/gson-2.10.1-sources.jar ../lib/lib/deployment/

Might not be necessary.
Add this Quarkus config property ../config/quarkus.properties
quarkus.http.limits.max-form-attribute-size=50M

## How does it work?

If this authenticator gets configured as part of a browser based login flow, Keycloak will do:

- Enabling passkey autofill when supported by the browser.

- Displaying the “Sign with passkeys” button if passkey autofill is not available.

- If Passkeys (Webauthn) are not supported, it will present the traditional username and password login option.

Here's an example of the Browser login flow:   
    <img src="docs/idp-flow-2.png" width="80%" height="80%">

Here's the default  user experience when trying to login with Passkeys, which is a dialog that pops up. In some cases the UX is not good.
- Default user experience with Passkeys   
<img src="docs/default-passkey.png" width="40%" height="40%">

By using the feature called conditional UI (a part of the WebAuthn standard), you can enhance the UX. You can include passkeys in autofill suggestions.
- Improved Passkeys with suggestions   
<img src="docs/autofill-passkey.png" width="80%" height="60%">

> **Considerations**:   
> Check out the [Passkeys Browser Autofill UI Device Support link](https://passkeys.dev/device-support/). So far, Ubuntu doesnt not support Passkey Autofill UI.   
>   

## How to install?

Build with:
```bash
mvn clean package
```

Follow the below instructions depending on your distribution and runtime environment.

### Quarkus-based distro

Copy the jar to the `providers` folder and execute the following command:

```shell
${kc.home.dir}/bin/kc.sh build
```

### Container image (Docker)

For Docker-based setups mount or copy the jar to `/opt/keycloak/providers`.

> **Warning**:
>
> With the release of Keycloak 17 the Quarkus-based distribution is now fully supported by the Keycloak team.
> Therefore, <b>I have not tested this extension in Wildfly-based distro </b> :exclamation: ️


# Test Cases
The test cases are available in the workshop:

* Workshop: https://github.com/embesozzi/keycloak-workshop-stepup-mfa-biometrics
