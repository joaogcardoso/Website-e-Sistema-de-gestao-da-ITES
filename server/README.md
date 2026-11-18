# Backend

API do VH Studio, responsável por autenticação, persistência de projetos e
temas, publicação de sites e recebimento de formulários. A aplicação usa Java
25 e Spring Boot 4.1.1. O banco suportado pelo ambiente do projeto é PostgreSQL.

## Organização

O código fica em `src/main/java/br/com/vhstudio/server/` e acompanha a divisão
por domínio já usada no projeto:

- `controllers/`: endpoints REST e conversão dos parâmetros HTTP.
- `services/`: fluxos de aplicação, validação de regras, autorização por
  proprietário e limites transacionais.
- `repositories/`: consultas e comandos SQL focados em cada domínio.
- `models/` e `utils/dtos/`: modelos de persistência e contratos HTTP.
- `configs/`: segurança, OpenAPI e demais configurações Spring.
- `db/migration/`: migrações versionadas do Flyway.

A persistência usa `JdbcTemplate` e SQL explícito; o projeto não usa Spring Data
JPA. A maior parte do SQL fica nos repositories; os services de publicação e
envio de formulários também usam JDBC diretamente nos fluxos que orquestram.
As listagens paginadas buscam apenas a página solicitada e consultam a contagem
necessária para os metadados. A listagem de publicações agrega os dados de cada
publicação na consulta da página, evitando carregar versões e métricas em
consultas repetidas por item.

## Documentação e teste manual da API

O Springdoc gera a especificação OpenAPI a partir dos controllers e dos DTOs.
Com a aplicação local iniciada, abra:

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`
- OpenAPI YAML: `http://localhost:8081/v3/api-docs.yaml`

Se `SERVER_HOST_PORT` tiver outro valor no `.env`, use essa porta no endereço.
Os caminhos de documentação são públicos; os endpoints continuam aplicando a
segurança definida para cada operação.

Para testar operações protegidas pelo Swagger UI, faça login pelo endpoint
`POST /auth/login`, copie o `accessToken` da resposta e use **Authorize** com o
valor do token. O access token tem validade curta; renove a sessão pelo endpoint
`POST /auth/refresh`. Se a conta exigir dois fatores, conclua o desafio em
`POST /auth/login/2fa` antes de autorizar as chamadas.

Há dois contratos de autenticação. `/auth/*` oferece tokens no corpo da
resposta, o que facilita o teste manual e clientes que gerenciam tokens.
`/auth/browser/*` é o fluxo usado pelo frontend: trabalha com cookies HttpOnly,
valida o cabeçalho `Origin` e aplica as regras de sessão do navegador. Para
chamar essas operações pelo Swagger, a origem da própria UI precisa estar em
`AUTH_BROWSER_ORIGINS`; o `.env.example` inclui `http://localhost:8081` para o
ambiente padrão. Se a porta local mudar, atualize a origem correspondente. Em
produção, mantenha a lista restrita aos domínios reais do cliente.

O Swagger é uma ferramenta de desenvolvimento e exploração. Antes de publicar
o serviço, avalie se a especificação deve permanecer acessível no ambiente de
produção. Mesmo com a documentação liberada, operações privadas exigem um JWT
válido e continuam sujeitas às verificações de usuário e propriedade.
`OPENAPI_ENABLED=false` e `SWAGGER_UI_ENABLED=false` desativam os dois recursos;
no Compose os valores padrão são `true` para facilitar o trabalho local.

## Recursos da API

| Caminho | Operações e responsabilidade |
| --- | --- |
| `/auth/*` | Cadastro com confirmação, login, refresh e logout por token, recuperação de senha, confirmação de e-mail e perfil autenticado. |
| `/auth/browser/*` | Login e cadastro web, refresh e logout por cookies HttpOnly, alternância de contas e desafios de autenticação. |
| `/auth/account/*` | Sessões, troca de senha/e-mail, exclusão da conta e configuração de TOTP e códigos de recuperação. |
| `/projects` | Criar, listar, consultar, atualizar, renomear e excluir projetos da organização padrão do usuário autenticado. |
| `/projects/{id}/revisions` | Consultar o histórico paginado de revisões de rascunho de um projeto. |
| `/projects/{id}/revisions/{revisionId}/restore` | Restaurar uma revisão de rascunho identificada, criando uma nova revisão. |
| `/themes` | Listar, criar, atualizar e excluir temas salvos na organização padrão. |
| `/publications` | Listar publicações e métricas agregadas da organização padrão. |
| `/projects/{projectId}/publication*` | Solicitar e decidir revisões, publicar e desativar um site, restaurar versões, consultar análises, respostas de formulários e atividade. |
| `/organizations/{organizationId}/templates*` | Consultar, versionar e arquivar modelos próprios da organização com autoria explícita. |
| `/organizations/{organizationId}/audit-events` | Consultar a trilha paginada da organização; exige `ORGANIZATION_AUDIT_READ` explícito. |
| `/public/{key}` | Entregar páginas publicadas e receber eventos de interação. |
| `/public/{key}/submissions` | Receber respostas vinculadas à versão de formulário do snapshot público; a organização vem da chave da publicação. |
| `/actuator/health` | Health check usado pelo Compose e pela infraestrutura. |

Os endpoints seguem os contratos dos controllers. Uma remoção sem corpo retorna
`204`; criação de recursos retorna `201` quando definido pelo endpoint. A
validação estrutural usa Jakarta Bean Validation e as regras de domínio são
aplicadas nos services.

## Paginação

Os parâmetros de consulta usam índice iniciado em zero. Os tamanhos padrão e
máximos atuais são:

| Coleção | Tamanho padrão | Tamanho máximo |
| --- | ---: | ---: |
| Projetos (`GET /projects`) | 10 | 10 |
| Histórico de rascunho (`GET /projects/{id}/revisions`) | 10 | 50 |
| Publicações (`GET /publications`) | 10 | 10 |
| Temas (`GET /themes`) | 20 | 20 |
| Sessões (`GET /auth/account/sessions`) | 10 | 20 |
| Envios e atividades de uma publicação | 10 | 10 |
| Eventos de auditoria da organização | 50 | 100 |

Exemplo: `GET /projects?page=0&size=10&sort=recent&q=site`. Projetos e
publicações aceitam busca e ordenação conforme o endpoint. O servidor rejeita
página negativa, tamanho fora do limite e offsets acima de 1.000.000.

Listas paginadas retornam este formato:

```json
{
  "items": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0,
  "hasNext": false,
  "hasPrevious": false
}
```

Métricas de atividade diária são séries limitadas, não páginas de eventos. As
consultas de versões de publicação seguem a retenção descrita em
[revisões de projeto e versões publicadas](#revisoes-de-projeto-e-versoes-publicadas).

## Revisões de projeto e versões publicadas

Cada salvamento do projeto cria uma revisão `DRAFT` identificada e imutável.
Salvar ou restaurar um rascunho altera o documento de trabalho, mas não muda a
versão servida no site público. A listagem de revisões retorna somente itens
`DRAFT`, ordenados da revisão mais recente para a mais antiga:

```http
GET /projects/{id}/revisions?page=0&size=10
POST /projects/{id}/revisions/{revisionId}/restore
```

A listagem exige `PROJECT_READ`; a restauração exige `PROJECT_UPDATE` e
`PROJECT_READ`. A paginação usa índice iniciado em zero, tamanho padrão 10 e
máximo 50. Restaurar exige o UUID de uma revisão do mesmo projeto e grava seu
conteúdo como uma nova revisão atual. A revisão escolhida e as revisões
posteriores permanecem no histórico; a operação não apaga revisões.

Uma publicação aponta para o snapshot exato usado para gerar cada versão. Os
objetos de versão incluem `projectRevision` (número da revisão de rascunho) e
`projectRevisionId` (UUID estável do snapshot associado). O conteúdo público é
servido da versão publicada atual. Portanto, salvar ou restaurar um rascunho
não altera a publicação existente.

Restaurar uma versão publicada é uma operação distinta da restauração de
rascunho. `POST /projects/{projectId}/publication/versions/{version}/restore`
cria a próxima versão pública copiando o snapshot identificado pela versão
selecionada; não altera o documento do projeto nem cria uma revisão `DRAFT`.
Essa operação mantém o vínculo com o `projectRevision` e o
`projectRevisionId` do snapshot de origem. O corpo exige `reviewRequestId` de
uma solicitação aprovada para o mesmo snapshot e versão.

Toda nova publicação ou restauração exige uma revisão explícita vinculada a um
UUID imutável de revisão. Crie a solicitação com
`POST /projects/{projectId}/publication/review-requests`, enviando
`projectRevisionId`, `operation` (`PUBLISH` ou `RESTORE`) e, para restauração,
`targetVersion`. Para `PUBLISH`, a revisão deve ser o rascunho atual; para
`RESTORE`, deve ser o snapshot retido da versão indicada. A listagem usa `GET`
no mesmo caminho. Uma pessoa diferente da solicitante, com grant
`PUBLICATION_APPROVE` no escopo do projeto ou organização, decide em
`POST /projects/{projectId}/publication/review-requests/{reviewRequestId}/decision`
com `approved`. A publicação recebe `{ "reviewRequestId": "..." }` no POST;
a restauração recebe o mesmo corpo. Uma aprovação só pode ser consumida uma vez
e para a mesma operação, revisão e versão solicitadas. Rejeições não autorizam
a operação. Desativar uma publicação continua exigindo `PUBLICATION_MANAGE` e
não requer revisão.

A caixa de entrada `GET /publication/review-requests` exige vínculo ativo e
`PUBLICATION_APPROVE` em pelo menos um projeto ou na organização selecionada.
Ela retorna somente solicitações pendentes que o ator pode decidir, sem
documento, preview geral de publicação ou identidade de outras pessoas. A rota
`/publication/reviews` é a entrada da interface. Para inspecionar o conteúdo,
`GET /projects/{projectId}/publication/review-requests/{reviewRequestId}/preview`
exige o mesmo grant no projeto, uma solicitação pendente de outra pessoa e
retorna apenas o documento da revisão imutável vinculada ao pedido. Isso não
concede `PUBLICATION_READ` nem acesso a outras publicações.

As publicações ativas existentes permanecem ativas durante a migração. A
`V17` não altera registros de publicação ou grants existentes e não concede a
nova ação ao baseline `OWNER`.

A versão corrente não tem prazo de expiração e permanece consultável na gestão;
ela é servida publicamente enquanto a publicação estiver ativa. O prazo de até
30 dias aplica-se somente às versões anteriores depois que são substituídas.
A consulta e a restauração abrangem no máximo os três números de versão mais
recentes. O publish e o restore de publicação removem, na mesma transação,
versões anteriores expiradas ou fora desse limite e os snapshots
`PUBLICATION_SNAPSHOT` sem outra versão publicada retida que os referencie. A
migração inicial também descarta versões que já estavam fora dessa janela. A
remoção física durante a operação é sob demanda: não há tarefa agendada de
expurgo. Assim, versões e snapshots expirados podem permanecer no banco em
repouso até o próximo publish ou restore daquela publicação. A API não lista
nem restaura versões expiradas, e a rota pública serve somente a versão corrente
quando a publicação está ativa. Revisões `DRAFT` não têm expurgo e não são
removidas por essa limpeza.

### Formulários e respostas

Os blocos de formulário continuam aceitando o schema legado em `props.formFields`
e os schemas padrão existentes. Ao publicar, cada bloco de formulário vira um
recurso associado à organização e ao projeto, identificado pela página e pelo
bloco de origem. Alterações de título, definição ou campos criam uma nova versão
imutável; uma publicação reutiliza a versão atual quando esses dados não mudaram.
Cada snapshot publicado guarda as referências explícitas (`formId` e
`formVersionId`) de seus formulários. Restaurar uma publicação também restaura
essas referências junto com o documento.

`POST /public/{key}/submissions` recebe `pageId`, `blockId`, a versão da
publicação e, para clientes atuais, `formVersionId`. O servidor confere a
referência contra o snapshot ativo e valida os valores usando o schema daquela
versão. Para compatibilidade com páginas já abertas antes da atualização,
`formVersionId` pode ser omitido: o servidor resolve a versão na referência do
snapshot. O corpo não aceita nem consulta um tenant enviado pelo visitante; a
organização vem exclusivamente da publicação identificada pela chave pública.

A migração `V18` cria recursos e versões para formulários em snapshots ainda
retidos e vincula respostas antigas às versões correspondentes. Respostas cujos
snapshots já expiraram recebem uma versão histórica de compatibilidade a partir
do `field_schema` já salvo na resposta. Os campos legados usados pela inbox e
exportação permanecem disponíveis. Os grants T02 de leitura e exportação de
respostas não são alterados.

A `V19` garante por chaves estrangeiras compostas que cada versão publicada e
cada solicitação de revisão use uma revisão do mesmo projeto. Também restringe
grants derivados ao mesmo tenant do grant pai. A coluna de projeto das versões
publicadas é preenchida a partir da publicação associada.

A `V20` registra a organização nas publicações e respostas, derivando-a do
projeto/publicação já associado. Chaves estrangeiras compostas impedem que uma
resposta seja vinculada a uma publicação ou versão de formulário de outra
organização.

A `V21` cria a trilha de auditoria da organização e a ação
`ORGANIZATION_AUDIT_READ`. O endpoint e os limites dessa concessão estão em
[contexto da organização ativa](../docs/contexto-organizacao.md#trilha-de-auditoria).
A `V22` preserva o nome de cada versão de formulário, inclusive para versões
históricas migradas, em vez de depender do nome atual do recurso.

O isolamento em runtime combina membership/grants verificados pelos services,
consultas de recursos vinculadas ao projeto ou à organização e essas constraints
compostas no PostgreSQL. Não foi habilitado Row Level Security: a aplicação usa
uma conexão JDBC compartilhada e não estabelece um tenant confiável por
transação, requisito para políticas RLS que protejam também conexões reutilizadas
no pool. Além disso, o Compose local usa `postgres` como papel padrão, que pode
ignorar políticas RLS. Sem confirmar o papel e o contexto transacional da
implantação real, RLS não oferece aqui uma defesa adicional verificável; reavalie
quando a aplicação usar um papel runtime de privilégio mínimo e definir o tenant
de forma segura em cada transação.

## Banco e configuração

O Flyway aplica as migrações versionadas `V1` a `V22` na inicialização. Não edite migrações
que já tenham sido executadas em outros ambientes: crie uma nova migração
versionada em `src/main/resources/db/migration/`.

A migração `V9` cria organizações e vínculos de usuários, cria uma organização
padrão para cada conta existente e associa projetos e temas sem alterar seus
conteúdos. A `V10` adiciona grants explícitos e semeia permissões equivalentes
para os vínculos existentes. Novas contas recebem grants-base na mesma
transação do cadastro. `OWNER` e `MEMBER` continuam identificando o vínculo;
runtime autoriza cada operação por ação concedida, não pelo nome do papel.

### Grants de organização

`GET /organizations/{organizationId}/grants` lista grants paginados e exige
`ORGANIZATION_GRANTS_READ`. `POST` no mesmo caminho cria um grant derivado de
um `parentGrantId` delegável; `DELETE /organizations/{organizationId}/grants/{grantId}`
revoga o grant e toda a sua descendência e exige `ORGANIZATION_GRANTS_MANAGE`.
O beneficiário precisa ser membro da organização. O corpo de criação especifica
`beneficiaryUserId`, `action`, `scopeType`, `scopeId`, `validUntil` opcional,
`canDelegate` e `parentGrantId`.

Os grants-raiz `PUBLICATION_SUBMISSIONS_EXPORT` e `PUBLICATION_APPROVE` são
exceções controladas: exigem
`authorityGrantId` apontando para um `ORGANIZATION_GRANTS_MANAGE` ativo,
organizacional, delegável e autorizado para o emissor. A validade da exportação
não pode superar a menor validade da autoridade e de seus ancestrais. A
aprovação só pode abranger a organização ou um projeto dentro dela, e segue o
mesmo limite de validade. Sua concessão é explícita: não há backfill nem inclusão
no baseline OWNER. A origem fica registrada separadamente. Revogar a autoridade
revoga os grants-raiz que ela autorizou. A `V17` adiciona a ação sem backfill.

### Modelos próprios de página

`GET/POST /organizations/{organizationId}/templates` lista e cria modelos da organização. `GET /organizations/{organizationId}/templates/{templateId}/versions` lista snapshots imutáveis; `POST` no mesmo caminho cria uma nova versão com `expectedVersion`; `DELETE /organizations/{organizationId}/templates/{templateId}` arquiva o modelo. Todas as rotas exigem membership ativo. Somente a pessoa autora pode criar versões ou arquivar; grants de projetos não são reutilizados para esse domínio. Consulte [modelos de página por organização](../docs/modelos-de-pagina.md) para os corpos, limites e regras de compatibilidade.

Não há rota para mutar modelos globais: a decisão de governança T00 ainda não autorizou essa autoridade.

As ações disponíveis são `PROJECT_READ/CREATE/UPDATE/DELETE`,
`THEME_READ/CREATE/UPDATE/DELETE`, `PUBLICATION_READ/MANAGE`,
`PUBLICATION_ANALYTICS_READ`, `PUBLICATION_SUBMISSIONS_READ`,
`PUBLICATION_SUBMISSIONS_EXPORT`, `PUBLICATION_APPROVE`, `PUBLICATION_ACTIVITY_READ`,
`ORGANIZATION_GRANTS_READ`, `ORGANIZATION_GRANTS_MANAGE` e
`ORGANIZATION_AUDIT_READ`. Escopos são `ORGANIZATION`, `PROJECT` e `THEME`.
Um grant derivado mantém a ação do pai, restringe-se ao mesmo recurso ou a um
escopo menor, não pode estender sua validade nem aumentar `canDelegate`. Não
há profundidade numérica fixa; cada nível continua limitado pelo pai. Revogar
um grant revoga os descendentes, enquanto concessões independentes permanecem
válidas. Grants vencidos deixam de autorizar mesmo que o estado persistido
continue `ACTIVE`.

Páginas, interações e envios públicos continuam acessíveis por chave sem
autenticação; a leitura autenticada de submissões exige
`PUBLICATION_SUBMISSIONS_READ`.
Exportar respostas usa `GET /projects/{projectId}/publication/submissions/export`
e exige `PUBLICATION_SUBMISSIONS_EXPORT`, independente da permissão de leitura.

### Membros e convites

`GET /organizations/{organizationId}/members` e `/invitations` listam os
registros do tenant e exigem `ORGANIZATION_GRANTS_READ`. `POST` em
`/organizations/{organizationId}/invitations` envia um link de uso único válido
por sete dias e exige `ORGANIZATION_GRANTS_MANAGE`; `DELETE` no convite revoga
convites pendentes. A consulta pública do token usa
`GET /organizations/invitations/lookup?token=...`; a aceitação autenticada usa
`POST /organizations/invitations/accept` e exige e-mail verificado igual ao
endereço convidado.

Memberships registram `ACTIVE` ou `REVOKED`; convites registram `PENDING`,
`ACCEPTED`, `EXPIRED` ou `REVOKED`. O papel `MEMBER` é descritivo, e toda
autorização continua dependendo dos grants. Revogar um membership também revoga
os grants do membro e toda a descendência. A `V11` mantém os registros revogados
para exibição no histórico.

As configurações de runtime vêm de variáveis de ambiente:

| Variável | Uso |
| --- | --- |
| `DB_HOST`, `DB_PORT`, `DB_DATABASE`, `DB_USERNAME`, `DB_PASSWORD` | Conexão PostgreSQL. |
| `AUTH_JWT_SECRET` | Segredo Base64 de pelo menos 32 bytes aleatórios para assinar JWTs. |
| `AUTH_TOTP_ENCRYPTION_KEY` | Chave Base64 separada para proteger segredos TOTP. |
| `AUTH_BROWSER_ORIGINS` | Origens permitidas nos fluxos de autenticação do navegador. |
| `AUTH_COOKIE_SECURE` | Habilita cookies `Secure` em HTTPS. |
| `OPENAPI_ENABLED`, `SWAGGER_UI_ENABLED` | Habilitam a especificação e a interface Swagger. |
| `MAILER_ENABLED`, `RESEND_API_KEY`, `MAIL_FROM_ADDRESS` | Envio de e-mail transacional via Resend. |
| `APP_BASE_URL` | Endereço usado nos links enviados por e-mail. |

Com o mailer desligado, os e-mails de conta são descartados; um gateway de
teste é necessário para percorrer localmente os fluxos que dependem de links.
Os nomes e valores padrão do Compose estão em `.env.example`.

## Executar e validar

O fluxo integrado recomendado está no README da raiz. Para compilar ou validar
o backend separadamente:

```sh
cd server
./mvnw --batch-mode verify
```

Os testes de integração usam Testcontainers e precisam de um Docker funcional.
O container de produção compila com JDK 25 e executa com a imagem JRE 25.
