# Auth0 resource-server authentication

The React app uses the Auth0 SPA SDK with Authorization Code Flow + PKCE. Configure
`VITE_AUTH0_DOMAIN`, `VITE_AUTH0_CLIENT_ID`, and `VITE_AUTH0_API_AUDIENCE` in `.env`
(copy `.env.example` first). Register the frontend origin, such as
`http://localhost:5173`, in the Auth0 application's Allowed Callback URLs, Allowed
Logout URLs, and Allowed Web Origins.

Create an Auth0 API whose identifier exactly matches `VITE_AUTH0_API_AUDIENCE`. Set the
backend's `AUTH0_ISSUER_URI` and `AUTH0_API_AUDIENCE` to that tenant and API identifier.
The backend needs no OAuth client secret and does not implement a login callback or
session. It validates the access token's signature, issuer, and audience as a resource
server.

The API associates Auth0 accounts with BreakMarket users by verified email. Add a Post
Login Action in Auth0 that adds the following namespaced claims to the API access token:

```js
exports.onExecutePostLogin = async (event, api) => {
  const namespace = 'https://breakmarket-api/'
  api.accessToken.setCustomClaim(`${namespace}email`, event.user.email || '')
  api.accessToken.setCustomClaim(`${namespace}email_verified`, event.user.email_verified === true)
  api.accessToken.setCustomClaim(`${namespace}name`, event.user.name || '')
  api.accessToken.setCustomClaim(`${namespace}nickname`, event.user.nickname || '')
}
```

Set `APP_CORS_ALLOWED_ORIGINS` to the exact frontend origin(s) when the browser calls an
API on a different origin. Access tokens stay in memory under the SPA SDK's default
cache and are sent as bearer tokens. Authentication cookies are not used.
