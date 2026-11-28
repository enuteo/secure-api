# secure-api

API protegida por JWT do Keycloak. O `GET /object/{id}` exige a role
`objectrole`; ela pode ser atribuída como realm role ou como client role do
client configurado em `KEYCLOAK_CLIENT_ID` (padrão: `auth-api-test`).

Por padrão, a API valida tokens do realm `authtest` em `http://localhost:8080`.
Para outro ambiente, configure `KEYCLOAK_ISSUER_URI`,
`KEYCLOAK_JWK_SET_URI` e, se necessário, `KEYCLOAK_CLIENT_ID`.

Além da role, o usuário precisa ser o dono do objeto ou estar acima do dono na
hierarquia. O nome de usuário vem da claim `preferred_username` e é resolvido em
`app_users`; `base_objects.owner_id` referencia `app_users.id`. A tabela
`user_hierarchy` armazena pares `(user_id, manager_id)`, e a API percorre a
hierarquia ascendente com `WITH RECURSIVE`.

Respostas de autorização: sem token, `407` com a mensagem `não há autenticação`;
sem a role exigida, `401` com `não autorizado a funcionalidade`; com role mas sem
acesso ao objeto, `403` com `não tem permissão de acessar o recurso`. Objetos
inexistentes continuam retornando `404`.
