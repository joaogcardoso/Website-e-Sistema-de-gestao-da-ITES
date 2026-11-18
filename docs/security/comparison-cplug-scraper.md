# Comparação de autorização com o cplug-scraper

## Objetivo e escopo

Este documento registra a inspeção do sistema de autorização do `cplug-scraper` no workspace e compara seus padrões com o ITES. O repositório de referência foi localizado como projeto irmão de `ites-manage-system` no workspace compartilhado. O código é usado somente como referência arquitetural; não há dependência entre aplicações.

## Arquiteturas observadas

| Aspecto | ITES antes desta evolução | cplug-scraper | Diretriz para o ITES |
|---|---|---|---|
| Persistência | PostgreSQL com `app_roles`, `app_permissions`, `app_user_roles` e `app_role_permissions`; autorização adicional por escopo organizacional | PostgreSQL com `auth_roles`, `auth_permissions`, associações de usuário/papel e papel/permissão | Manter os nomes, o modelo relacional e os escopos existentes no ITES |
| Resolução | `ItesAccessService` consulta as associações do banco em cada operação protegida | Serviço de autenticação hidrata papéis e permissões da sessão; middleware exige permissão no servidor | Continuar usando `ItesAccessService` como autoridade nas operações ITES; não confiar em permissões enviadas pelo cliente |
| Rotas | Controladores ITES chamam `require` para grants administrativos | Rotas aplicam middleware `requirePermission` | Exigir grants no backend, inclusive em agregações e exportações |
| Interface | `/ites/me/permissions` fornece grants efetivos; páginas condicionam ações e navegação | Sessão autenticada inclui grants; rotas e navegação usam `hasPermission` | Usar os grants efetivos para apresentação, mantendo validação de servidor |
| Provisionamento | `InitialAdministratorProvisioner` associa a conta configurada ao papel `ADMINISTRATOR`; V23 atribuía apenas os grants existentes naquele momento | `bootstrap-admin.cjs` serializa o primeiro provisionamento com lock transacional; a migration inicial atribui as permissões presentes ao papel `admin` | Preservar o provisionador ITES e tornar a sincronização de permissões idempotente no banco |
| Grants futuros | Uma permissão incluída após V23 não era automaticamente associada ao administrador | Novas permissões posteriores também exigem associação explícita em migrations | Adicionar sincronização do papel canônico `ADMINISTRATOR` quando novas permissões forem cadastradas |

## Padrões úteis observados no cplug-scraper

- Associação explícita entre usuários, papéis e permissões, sem confiar em um nome de papel enviado pelo navegador.
- Middleware no backend para validar cada ação protegida.
- Permissões efetivas obtidas de associações persistidas.
- Provisionamento transacional do administrador e proteção contra duas inicializações concorrentes.
- Validação de concessões para impedir que um gestor atribua capacidades que não possui.

## Adaptação adotada

O ITES conserva `ItesAccessService`, `app_roles`, `app_permissions` e o fluxo de concessões existente. Uma migration associa ao papel `ADMINISTRATOR` todas as permissões atuais e um gatilho associa automaticamente cada nova permissão inserida ao mesmo papel. A mudança não concede privilégios adicionais a solicitantes ou a outros papéis. O provisionamento da conta inicial continua sendo responsabilidade de `InitialAdministratorProvisioner`.

O escopo organizacional do ITES continua separado dos grants administrativos globais. Em particular, permissões de consulta de demandas e relatórios não substituem a autorização por organização das respostas de formulários de projetos.

## Limites da comparação

A comparação cobre RBAC, validação de rotas, entrega dos grants à interface e provisionamento inicial. Lógica de scraping, lojas, execuções, logs e outras regras de negócio do repositório de referência não se aplicam ao ITES e não são reutilizadas.
