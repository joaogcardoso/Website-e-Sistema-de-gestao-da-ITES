# Estado atual de implementação

A arquitetura executável está em `web/`, `server/` e `database/`. A base de autenticação e editor do `web-builder` foi integrada; os fluxos ITES de portal, organização, demanda, administração, permissões e conteúdo institucional foram adicionados sobre essa aplicação. A proposta da base de dados foi validada pelo responsável do projeto nesta conversa.

## Implementado

- Portal público com sete áreas institucionais e páginas publicáveis.
- Cadastro autenticado por confirmação de e-mail, login e sessão usando o fluxo existente.
- Cadastro de organizações e associação do solicitante.
- Envio e acompanhamento de demandas persistentes com código, urgência, serviço e condição por solicitação.
- Gestão administrativa com filtros, classificação, decisões justificadas e histórico.
- Papéis, permissões e concessão/revogação controlados no backend.
- Editor institucional com lista segura de blocos, rascunho versionado, prévia, publicação e retirada do ar.
- Docker Compose com serviços de frontend, backend e PostgreSQL, volume persistente e proxy interno.
- Workflow GitHub Actions com lint sem avisos, testes, builds, smoke test Compose, teste de persistência de volume, job agregado recomendado como verificação obrigatória e etapa de deploy Dokploy após verificação bem-sucedida.

## Validação executada

- Frontend: TypeScript, ESLint, 50 testes e build Vite aprovados.
- Backend: `./mvnw --batch-mode verify` executado em JDK 25 via contêiner; 40 testes aprovados, inclusive testes integrados ITES com PostgreSQL 16.
- Migrações: Flyway aplicou V1–V23 em banco limpo PostgreSQL 16.
- Compose: configuração, build e inicialização local realizados. PostgreSQL, API e frontend ficaram saudáveis; HTML em pt-BR, sete páginas públicas, 401 em rota protegida e retenção de um registro após recriação dos contêineres foram verificados. A migração registrou 23 versões e 11 permissões iniciais.

## Pendências

- Os scripts acadêmicos em `database/coursework/` e o diagrama foram executados em PostgreSQL 16, com evidências de consultas, rotinas, permissões e contas LOGIN. O modelo permanece separado das 35 tabelas operacionais; 4 papéis e 13 permissões são exceção semântica documentada e aguardam aceite do orientador.
- As permissões do banco de dados em runtime da aplicação ainda usam a conta proprietária; separar credenciais de migração/runtime antes de produção.
- Cadastro por e-mail exige chave e domínio Resend válidos; sem envio de e-mail, novas contas não completam a confirmação.
- Não foram validados testes de navegador ponta a ponta, acessibilidade manual nem publicação em Dokploy/VPS.
- A proteção da branch não pode ser definida por arquivo. Administrador do GitHub precisa tornar `CI / ci-success` obrigatória em `main`, exigir PR e branch atualizada.
- O PDF original e o modelo institucional não foram encontrados.

A aplicação está em fase de MVP funcional, mas a implantação de produção e os entregáveis acadêmicos completos ainda não estão concluídos. Consulte [estado detalhado de implementação](IMPLEMENTATION_STATUS.md), [CI e bloqueio de PR](ci.md) e [implantação](deployment.md).
