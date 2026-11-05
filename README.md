# secure-api

API protegida por JWT do Keycloak. O `GET /object/{id}` exige a role
`objectrole`; ela pode ser atribuída como realm role ou como client role do
client configurado em `KEYCLOAK_CLIENT_ID` (padrão: `auth-api-test`).

Por padrão, a API valida tokens do realm `authtest` em `http://localhost:8080`.
Para outro ambiente, configure `KEYCLOAK_ISSUER_URI`,
`KEYCLOAK_JWK_SET_URI` e, se necessário, `KEYCLOAK_CLIENT_ID`.
