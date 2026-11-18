# Arquitetura executável integrada

O `web-builder` fornece a base única para frontend React/TypeScript/Vite, autenticação por cookie HttpOnly e JWT, API Spring Boot, persistência PostgreSQL/Flyway, componentes e catálogo de blocos, editor, prévia e publicação. Os diretórios `web/` e `server/` concentram os módulos; o projeto mantém a divisão por responsabilidade e os contratos de origem usados por essas capacidades.

A V23 acrescenta `app_roles`, `app_permissions`, associações usuário/papel e papel/permissão, auditoria de concessões, `demands`, `demand_events`, `institutional_pages`, `institutional_page_versions` e auditoria de publicação. Reutiliza as tabelas existentes `users`, `organizations` e `organization_memberships`; não cria outro cadastro. Os papéis da ITES não derivam de afiliação. A conta administradora inicial recebe papel por operação manual descrita no README.

A API em `/ites/demands` usa o JWT para identificar solicitantes e restringe consultas ao proprietário. As rotas `/review`, `/classification` e `/decision` verificam permissões explícitas. Transições e eventos ficam na mesma transação e o gatilho valida o grafo de situações. `/ites/organizations` lista associações do usuário e verifica o vínculo antes de vincular uma organização à demanda. `/ites/roles` e `/ites/users` exigem concessões administrativas.

As rotas públicas `/ites/pages` e `/ites/pages/{slug}` listam e entregam somente páginas publicadas. Rotas draft/save exigem `builder.pages.edit`; publish/unpublish exige `builder.pages.publish`. Documento JSON é limitado a uma página, até 20 blocos permitidos, campos textuais permitidos e URLs seguras. A interface usa `BuilderDocument`, `reduceDocument`, o catálogo `definitions` e `PageContent` do editor existente; a API não executa código do documento.

Flyway em `server/src/main/resources/db/migration/` é a única fonte operacional de migrações. Os scripts `database/coursework/` são artefatos acadêmicos e ainda não foram reconciliados com todo o esquema integrado; não devem ser aplicados como segunda fonte de schema.
