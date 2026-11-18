# Estado de implementação e ponto de retomada

Data da atualização: 18/11/2026. Este documento registra o estado observado no repositório e deve ser lido antes de retomar o desenvolvimento. A proposta do banco de dados foi aprovada pelo responsável do projeto nesta conversa. Essa aprovação permite continuar a implementação; não equivale à validação de produção ou à aprovação de uma implantação.

## Estado do Git

- Branch: `main`.
- Os marcos de backend, frontend e CI estão registrados nos commits `63df021`, `4b90c74` e `f0048bf`, sobre a contribuição do colaborador `c903490`. Consulte `git log` para verificar o HEAD e commits posteriores de documentação.
- Identidade Git verificada: identidade do proprietário configurada local e globalmente.
- As alterações de aplicação e pipeline deste marco foram commitadas; confira `git status` para mudanças posteriores.

## O que foi integrado

O commit `f7410fb` incorporou os diretórios executáveis `web/` e `server/` do repositório `web-builder`, além de `compose.yaml`. A base traz React/TypeScript/Vite, catálogo e editor visual de blocos, autenticação com sessão baseada em cookie e JWT, Spring Boot, PostgreSQL, JdbcTemplate e Flyway, incluindo as migrações V1–V22 e testes que já existiam no repositório de origem. A integração aproveitou componentes coesos do editor e da autenticação, mas trouxe um conjunto extenso de funcionalidades do VH Studio que ainda não foi reduzido ao escopo da ITES.

A configuração do Compose remove a publicação da porta do banco e da API, deixando o frontend como entrada pública e encaminhando `/api/` à API pela rede Docker. O Nginx encaminha para `ites-api`. O banco persiste em volume. O `README.md` contém instruções locais e configuração do provedor Resend para confirmação de cadastro.

## Funcionalidades ITES implementadas

`V23__ites_domain.sql` acrescenta setor econômico às organizações já existentes, papéis/permissões da aplicação, demandas, histórico, páginas institucionais, versões JSONB, auditoria de publicação e gatilho de transições de situação. Os papéis são distintos da associação institucional e o cadastro não atribui administrador automaticamente. A primeira atribuição administrativa está no script manual `database/bootstrap_admin.sql`.

Os serviços e controladores implementam permissões efetivas, criação/listagem de organizações associadas, envio e acompanhamento de demandas, classificação, decisões com justificativa e gestão de papéis. As páginas React incluem portal público com as sete áreas, cadastro/consulta de organização e demanda, análise administrativa, atribuição de papéis, edição institucional com os blocos `hero-classic`, `features-classic` e `contact-channels`, prévia, salvamento otimista, publicação e retirada do ar. As decisões e publicações têm autorização no backend. O documento do editor aceita somente uma página, até 20 blocos permitidos, propriedades textuais controladas e URLs limitadas.

Os testes integrados executam as rotas com autenticação real e PostgreSQL: solicitante cria e consulta apenas suas demandas, administrador decide e gera histórico, páginas persistem e são publicadas e grants são verificados no servidor. A validação ainda não cobre automação de navegador nem persistência após recriação do contêiner.

## Arquivos principais da implementação

- `.github/workflows/ci.yml`: valida web, backend, build/health checks Compose e smoke checks; job agregado recomendado como bloqueio de PR e acionamento condicional do Dokploy.
- `compose.yaml`, `.env.example`, `README.md` e `docs/deployment.md`: execução local e VPS.
- `server/src/main/resources/db/migration/V23__ites_domain.sql`.
- `server/src/main/java/br/com/vhstudio/server/controllers/Ites/`: controladores de demanda, organizações, páginas, permissões, usuários e papéis.
- `server/src/main/java/br/com/vhstudio/server/services/Ites/`: autorização e regras de demanda.
- `server/src/main/java/br/com/vhstudio/server/configs/Security/SecurityConfig.java`: rota pública de leitura de páginas publicadas.
- `web/src/pages/ites/`: portal e jornadas ITES; o roteador foi integrado à navegação existente.
- `database/bootstrap_admin.sql`: atribuição inicial manual de administrador.

## Validações observadas

- Frontend: `npm run typecheck`, `npm run lint -- --max-warnings=0`, `npm test` (50 aprovados) e `npm run build` passaram após as telas ITES.
- Backend: `./mvnw --batch-mode verify`, em contêiner JDK 25, aprovou 40 testes (zero falhas/erros). Flyway aplicou V1–V23 pelo fluxo real da aplicação em PostgreSQL 16 limpo.
- Docker Compose: configuração e build das imagens passaram. A execução local deixou PostgreSQL, API e web saudáveis; foram verificados HTML em pt-BR, sete páginas públicas e HTTP 401 em endpoint protegido. Banco/API sem portas públicas. A aplicação registrou 23 migrações e 11 permissões iniciais.
- Banco acadêmico: `database/coursework/run_all.sql` executado em PostgreSQL 16 limpo; as 11 consultas, cinco procedures, três functions, três triggers, constraints e papéis foram validados. As tabelas com dados operacionais têm ao menos 20 registros; `roles=4` e `permissions=13` são exceções semânticas documentadas. Quatro contas LOGIN também foram autenticadas em contêiner descartável.
- O workflow foi analisado sintaticamente; os passos Compose/smoke e a retenção de um registro após recriação foram executados localmente. A pipeline ainda precisa de execução no GitHub. A branch protection não pode ser ativada por este checkout e depende de configuração administrativa no GitHub.

## Limitações e riscos conhecidos

1. A integração base trouxe 774 arquivos e mais de 100 mil linhas do VH Studio. Revisar o diff e remover somente funcionalidades realmente dispensáveis depois de mapear suas dependências, mantendo a aplicação única e sem diretórios por idade.
2. Cadastro exige e-mail de confirmação. Com `MAILER_ENABLED=false`, o provedor descarta mensagens e novas contas não concluem cadastro. O guia local precisa de credencial válida do Resend ou de um mecanismo local seguro de entrega de links.
3. A configuração do banco na aplicação usa uma conta proprietária para Flyway e runtime; privilégios mínimos ainda não foram implementados na stack Compose. Separar credenciais de migração e runtime e verificar acesso por tabela/rotina.
4. Os scripts acadêmicos em `database/coursework/` e o diagrama foram executados em PostgreSQL 16 dedicado com evidência real. Continuam como modelo separado e não correspondem às 35 tabelas físicas do produto. Quatro papéis e 13 permissões não chegam a 20 por serem catálogos finitos e semânticos; obter aceite acadêmico dessa exceção.
5. A trilha de auditoria de transições é escrita pelo serviço na mesma transação. O usuário runtime de banco pode fazer DML direto; separar privilégios e impedir alteração de situação sem histórico.
6. A UI de editor integra operações e renderização de blocos do `web-builder`; persistência/reabertura/publicação foi validada em teste integrado. A persistência de volume foi validada com registro sentinela após recriação; ainda não foi feita automação de navegador real.
7. Notícias, eventos, publicações, biblioteca e conteúdo institucional ainda são cartões estáticos; apenas páginas institucionais versionadas podem ser editadas pelo editor.
8. O PDF de requisitos original não foi encontrado. O código e a documentação usam a especificação reproduzida na solicitação.

## Próximos passos para retomar

1. Configurar no GitHub `CI / ci-success` como verificação obrigatória de PR e confirmar execução real da pipeline.
2. Reconciliar consultas, rotinas, carga de dados, privilégios e diagrama acadêmico com o schema operacional efetivo, sem criar uma segunda fonte de migração.
3. Executar testes de navegador, acessibilidade e persistência do volume após recriação.
4. Separar usuário/credenciais de runtime e migração do PostgreSQL.
5. Configurar segredos e ambiente protegido do Dokploy; validar implantação real em VPS e plano de rollback.

## Comandos úteis

```sh
cd web && npm run typecheck && npm run lint -- --max-warnings=0 && npm test && npm run build
cd ../server && ./mvnw --batch-mode verify
cd .. && DB_PASSWORD=... AUTH_JWT_SECRET=... AUTH_TOTP_ENCRYPTION_KEY=... docker compose config
cp .env.example .env
# Depois de preencher .env com segredos válidos:
docker compose up --build
```

O MVP funcional foi validado em testes e execução local; a conclusão integral permanece condicionada às pendências de segurança, entregáveis acadêmicos e implantação listadas acima.
