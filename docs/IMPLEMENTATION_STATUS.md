# Estado de implementação da plataforma ITES

Atualizado em 18/11/2026. Este documento registra as capacidades observadas e os pontos necessários para retomar o trabalho.

## Arquitetura atual

O repositório mantém frontend React/TypeScript/Vite em `web/`, backend Java/Spring Boot em `server/`, scripts relacionais e acadêmicos em `database/` e documentação em `docs/`. O PostgreSQL é a fonte de persistência. Flyway em `server/src/main/resources/db/migration/` é o único fluxo de migração operacional.

A interface combina o portal institucional, jornadas autenticadas e administração. O editor de páginas compartilha identidade, autorização e persistência com a API. A administração de concessões é aplicada no backend. O documento visual é validado e não executa código arbitrário.

## Funcionalidades disponíveis

- Cadastro com confirmação por e-mail, login, sessão e gestão de conta.
- Organizações, membros, papéis e permissões explícitas.
- Criação, classificação, decisão e acompanhamento de demandas com histórico.
- Páginas institucionais com versões de rascunho, prévia, publicação e retirada do ar.
- Compose com serviços web, API e PostgreSQL, volume persistente e verificações de saúde.
- Pipeline GitHub Actions com verificações de frontend, backend, imagens e execução de smoke tests.

## Validações registradas

- Frontend: verificação TypeScript, ESLint, testes automatizados e build aprovados na execução documentada.
- Backend: testes automatizados e integração com PostgreSQL 16 aprovados na execução documentada.
- Migrações: criação limpa e aplicação Flyway V1–V23 verificadas na execução documentada.
- Compose: configuração, build e inicialização local verificados; os serviços ficaram saudáveis e o banco permaneceu em volume após recriação.
- Banco acadêmico: consultas, procedures, functions, triggers, constraints e perfis foram executados em PostgreSQL 16 dedicado; os resultados estão na pasta de evidências do banco.

Esses resultados descrevem execuções anteriores e não substituem nova validação após as mudanças de marca deste marco.

## Limitações e riscos

1. O conteúdo de notícias, eventos, biblioteca e publicações ainda é parcialmente estático; as páginas institucionais versionadas são editáveis.
2. A publicação em Dokploy/VPS ainda não foi executada neste ambiente.
3. As credenciais operacionais do banco ainda precisam ser segregadas das credenciais que aplicam migrações antes de produção.
4. Os scripts acadêmicos e o diagrama ainda não representam o esquema operacional completo; catálogos semânticos finitos têm exceções de contagem documentadas.
5. A proteção obrigatória da branch depende de configuração no GitHub e da execução do workflow no remoto.
6. A criação de contas exige um provedor de e-mail configurado para confirmar o endereço.
7. O PDF original de requisitos e o modelo institucional não estavam disponíveis no repositório.

## Próximos passos

1. Validar as alterações de identidade com checks do frontend, testes integrados do backend e Compose.
2. Executar a pipeline no GitHub e configurar a verificação obrigatória da branch.
3. Completar reconciliação e aceite acadêmico do modelo de dados.
4. Separar credenciais de banco para migração e runtime.
5. Executar testes reais de navegador e preparar o ambiente de produção no Dokploy.
