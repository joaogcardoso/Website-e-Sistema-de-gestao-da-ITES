# Estado de implementação da plataforma ITES

Atualizado em 18/11/2026. Este documento registra o estado após a integração da landing page administrável, permissões sincronizadas e painel analítico de demandas.

## Arquitetura atual

O repositório mantém frontend React/TypeScript/Vite em `web/`, backend Java/Spring Boot em `server/`, scripts relacionais e acadêmicos em `database/` e documentação em `docs/`. O PostgreSQL é a fonte de persistência. Flyway em `server/src/main/resources/db/migration/` é o único fluxo de migração operacional.

A interface combina o portal institucional, jornadas autenticadas e administração. O editor de páginas compartilha identidade, autorização e persistência com a API. A administração de concessões é aplicada no backend. O documento visual é validado e não executa código arbitrário.

## Funcionalidades disponíveis

- Login, sessão e gestão de conta; cadastro público por e-mail opcional; provisionamento administrativo sem dependência de e-mail.
- Provisionamento do primeiro administrador por variáveis de ambiente, com e-mail marcado como verificado e sem segundo fator obrigatório.
- Organizações, membros, papéis e permissões explícitas.
- Criação, classificação, decisão e acompanhamento de demandas com histórico.
- Páginas institucionais com versões de rascunho, prévia, publicação e retirada do ar.
- Landing page inicial representada por documento versionado e publicado; quando o sistema é inicializado com administrador e sem versão pública, `InstitutionalHomeProvisioner` publica o recurso `ites-homepage.json`.
- Editor institucional reutiliza o catálogo e o `PropertyEditor` já integrados, com adição, remoção, reordenação, edição de propriedades, prévia e recuperação da versão publicada como rascunho.
- Papel `ADMINISTRATOR` recebe os grants cadastrados e grants futuros por trigger da migration V24; a V25 cadastra concessões analíticas e os demais papéis mantêm acesso explícito.
- Painel privado de demandas com filtros, indicadores, gráficos/tabelas configuráveis e persistência versionada no mesmo modelo de páginas, com grants dedicados.
- Relatórios individuais com histórico, exportação CSV filtrada e impressão do navegador para salvar como PDF.
- Respostas de formulários de projetos continuam consultáveis a partir da página da publicação, preservando a versão histórica do esquema e o controle por escopo organizacional/projeto.
- Compose com serviços web, API e PostgreSQL, volume persistente e verificações de saúde.
- Pipeline GitHub Actions com verificações de frontend, backend, imagens e execução de smoke tests.

## Validações registradas

- Frontend: `npm run typecheck`, `npm test` (50 testes), `npm run lint` e `npm run build` concluíram com sucesso. O Vite ainda emite um aviso de tamanho para os bundles `blocks` e `export` acima de 500 kB.
- Backend: `./mvnw clean test -B` executado em contêiner JDK com Docker/Testcontainers: 44 testes, sem falhas. Uma execução adicional de `./mvnw -Dtest=ItesFlowTests,ProjectFlowTests test -B` validou 25 testes, sem falhas, incluindo concessões futuras, filtros autorizados e auditoria de respostas.
- Migrações V24–V26: aplicadas com sucesso em bancos PostgreSQL 16 limpos nos testes integrados. A sincronização de permissões atuais/futuras, o catálogo analítico e a página privada do painel foram exercitados.
- Compose: `docker compose config --quiet`, `docker compose build server web` e `docker compose up -d --build` concluíram sem erros. Os três serviços estão saudáveis. A rota pública do portal e a API da página inicial responderam HTTP 200; o painel foi confirmado como privado (rota institucional HTTP 404 e API sem autenticação HTTP 401).
- Banco do Compose local: Flyway está na versão V26; o papel `ADMINISTRATOR` possui 17 de 17 permissões cadastradas; a página `inicio` está publicada com sete blocos e o registro do painel privado existe. A verificação não exibiu dados pessoais nem credenciais.
- Banco acadêmico: consultas, procedures, functions, triggers, constraints e perfis foram executados em PostgreSQL 16 dedicado; os resultados estão na pasta de evidências do banco.

As verificações desta seção foram realizadas em 18/11/2026. O build do backend no Dockerfile compila e empacota, mas não executa a suíte; os testes foram executados separadamente com Testcontainers. O ambiente de desenvolvimento em `http://localhost:3000` foi atualizado e permaneceu saudável após a recriação.

## Limitações e riscos

1. O conteúdo de notícias, eventos, biblioteca e publicações ainda é parcialmente estático; as páginas institucionais versionadas são editáveis.
2. A publicação em Dokploy/VPS ainda não foi executada neste ambiente.
3. As credenciais operacionais do banco ainda precisam ser segregadas das credenciais que aplicam migrações antes de produção.
4. Os scripts acadêmicos e o diagrama ainda não representam o esquema operacional completo; catálogos semânticos finitos têm exceções de contagem documentadas.
5. A proteção obrigatória da branch depende de configuração no GitHub e da execução do workflow no remoto.
6. O cadastro público por link e as notificações dependem de provedor de e-mail; contas criadas pela administração não dependem desse serviço.
7. O PDF original de requisitos e o modelo institucional não estavam disponíveis no repositório.

## Próximos passos

1. Executar testes reais de navegador para edição/publicação da landing page, consulta de respostas, painel e impressão em PDF.
2. Executar a pipeline no GitHub e configurar as verificações obrigatórias da branch protegida.
3. Completar reconciliação e aceite acadêmico do modelo de dados.
4. Separar credenciais de banco para migração e runtime antes da implantação de produção.
5. Preparar domínio, HTTPS, backups e segredos no Dokploy; nenhuma publicação em VPS foi executada.

## Limitações desta evolução

- O relatório em PDF usa a impressão do navegador e não é gerado no servidor.
- As visualizações analíticas agregam demandas. Respostas de formulários de projetos seguem suas páginas e autorizações atuais, sem agregação entre organizações.
- Os dashboards persistidos são privados e versionados; ainda não há compartilhamento público.
- A consulta administrativa apresenta até 500 demandas recentes; CSV está limitado a 10 mil registros por solicitação.
