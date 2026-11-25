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

The API associates Auth0 accounts with BreakMarket users by verified email. To provide
the claims that `GET /api/auth/me` expects:

1. In the Auth0 Dashboard, open **Actions → Library → Build Custom** and create a
   **Login / Post Login** action.
2. Add the action below. The claim names must match exactly, including the
   `https://breakmarket-api/` namespace and property suffixes.
3. Open **Actions → Flows → Login**, add the action to the flow, and **Apply** the flow.
4. Sign in again to get a new API access token, then check its payload. It must contain
   `https://breakmarket-api/email`, `https://breakmarket-api/email_verified`,
   `https://breakmarket-api/name`, `https://breakmarket-api/nickname`, and
   `https://breakmarket-api/roles`. Email must be valid and `email_verified` must be
   the boolean `true`. `roles` must be an array containing only `USER` and/or `ADMIN`;
   assign the `ADMIN` role in Auth0 to reviewers.

The action must run in the Login flow and set **access-token claims** (not only
ID-token claims):

```js
exports.onExecutePostLogin = async (event, api) => {
  const namespace = 'https://breakmarket-api/'
  api.accessToken.setCustomClaim(`${namespace}email`, event.user.email || '')
  api.accessToken.setCustomClaim(`${namespace}email_verified`, event.user.email_verified === true)
  api.accessToken.setCustomClaim(`${namespace}name`, event.user.name || '')
  api.accessToken.setCustomClaim(`${namespace}nickname`, event.user.nickname || '')

  const allowedRoles = ['USER', 'ADMIN']
  const assignedRoles = (event.authorization?.roles || [])
    .map(role => String(role).toUpperCase())
    .filter(role => allowedRoles.includes(role))
  api.accessToken.setCustomClaim(
    `${namespace}roles`,
    assignedRoles.includes('ADMIN') ? ['ADMIN'] : ['USER']
  )
}
```

The Auth0 API identifier must also match `VITE_AUTH0_API_AUDIENCE` exactly. The API
validates the token signature, audience, and issuer before reading the custom claims.
Every `GET /api/auth/me` validates the subject, verified email, and roles claim, then
synchronizes the user's email, name, username, and role from those claims. The local
database role is therefore a synchronized copy, not the source of authorization.
Reviewer API access is checked directly against the verified Auth0 role claim.

Set `APP_CORS_ALLOWED_ORIGINS` to the exact frontend origin(s) when the browser calls an
API on a different origin. Access tokens stay in memory under the SPA SDK's default
cache and are sent as bearer tokens. Authentication cookies are not used.
