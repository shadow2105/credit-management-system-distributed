import { PassedInitialConfig, LogLevel } from 'angular-auth-oidc-client';
import { environment } from '../../environments/environment';

/* Archived

// Public clients (like Single-Page Applications, Mobile Applications, Desktop Applications and 3rd Party Clients)
// cannot securely store sensitive information, making them potentially vulnerable to certain security risks.
// PKCE, stands for Proof Key for Code Exchange, is an extension to the OAuth 2.0 Authorization Code Flow
// that enhances security, especially for public clients. PKCE is designed to prevent a class of attacks known as Code Injection attacks.

// Client generates a 'Code Verifier' (an unguessable random string), and the 'Code Challenge' (a cryptographic hash (usually SHA-256) of the Code Verifier)
// Client includes the Code Challenge and a method used to create the challenge (e.g., "S256") in the initial authorization request.
// The Authorization server processes the request and, if the user grants consent, issues an authorization code.
// When the client exchanges the authorization code for an access token, it includes the original Code Verifier used to generate the Code Challenge.
// The Authorization server verifies that the Code Verifier matches the original Code Challenge it received during the initial request.
export const authConfig: PassedInitialConfig = {
  config: {
              authority: `${environment.issuerUri}`,
              redirectUrl: `${window.location.origin}/login/oauth2/code/cms`,
              postLoginRoute: 'dashboard',
              postLogoutRedirectUri: window.location.origin,
              clientId: '9DFD919F17AD2C97C24E543C3F954DD3',
              // No Client Secret as the Code Challenge acts as a dynamic secret
              scope: 'openid profile email address',
              responseType: 'code',
              silentRenew: true,
              silentRenewUrl: `${window.location.origin}/silent-renew.html`,
              // https://docs.spring.io/spring-authorization-server/reference/guides/how-to-pkce.html
              // https://github.com/spring-projects/spring-authorization-server/issues/297#issue-896744390
              useRefreshToken: false,
              renewTimeBeforeTokenExpiresInSeconds: 200,
              logLevel: environment.production ? LogLevel.None : LogLevel.Debug,
          }
}
*/