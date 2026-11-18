# Estado de implementação e ponto de retomada

Data da atualização: 18/11/2026. Este documento registra o estado observado no repositório e deve ser lido antes de retomar o desenvolvimento. A proposta do banco de dados foi aprovada pelo responsável do projeto nesta conversa. Essa aprovação permite continuar a implementação; não equivale à validação de produção ou à aprovação de uma implantação.

## Estado do Git

- Branch: `main`.
- HEAD e `origin/main`: `f7410fb` (`feat(core): incorpora base de autenticação e editor visual`). O remoto está sincronizado até esse commit; as alterações abaixo ainda não foram commitadas nem enviadas.
- Identidade Git verificada: identidade do proprietário configurada local e globalmente.
- Diretório de trabalho contém as mudanças não commitadas listadas na próxima seção. Preservá-las ao retomar.

## O que foi integrado

O commit `f7410fb` incorporou os diretórios executáveis `web/` e `server/` do repositório `web-builder`, além de `compose.yaml`. A base traz React/TypeScript/Vite, catálogo e editor visual de blocos, autenticação com sessão baseada em cookie e JWT, Spring Boot, PostgreSQL, JdbcTemplate e Flyway, incluindo as migrações V1–V22 e testes que já existiam no repositório de origem. A integração aproveitou componentes coesos do editor e da autenticação, mas trouxe um conjunto extenso de funcionalidades do VH Studio que ainda não foi reduzido ao escopo da ITES.

A configuração do Compose remove a publicação da porta do banco e da API, deixando o frontend como entrada pública e encaminhando `/api/` à API pela rede Docker. O Nginx encaminha para `ites-api`. O banco persiste em volume. O `README.md` foi reescrito com instruções locais e configuração do provedor Resend para confirmação de cadastro. Essas alterações estão no diretório de trabalho e precisam ser revisadas.

## Funcionalidades ITES em desenvolvimento

Existe a migração ainda não commitada `V23__ites_domain.sql`, que acrescenta setor econômico às organizações já existentes, papéis/permissões da aplicação, demandas, histórico, páginas institucionais, versões JSONB, auditoria de publicação e gatilho de transições de situação. Os papéis são distintos da associação institucional e o cadastro não atribui administrador automaticamente. A primeira atribuição administrativa está prevista no script manual `database/bootstrap_admin.sql`.

Foram criados, ainda sem commit, serviços e controladores para permissões efetivas, criação/listagem de organizações associadas, envio e acompanhamento de demandas, classificação, decisões com justificativa e gestão de papéis. As páginas React incluem portal público com as sete áreas, cadastro/consulta de organização e demanda, análise administrativa, atribuição de papéis, edição institucional com os blocos `hero-classic`, `features-classic` e `contact-channels`, prévia, salvamento otimista, publicação e retirada do ar. As decisões e publicações têm autorização no backend. O documento do editor aceita somente uma página, até 20 blocos permitidos, propriedades textuais controladas e URLs limitadas.

Esses fluxos ainda não foram demonstrados de ponta a ponta com autenticação real. Não declarar M1–M6, grants ou editor completos até a validação integrada.

## Alterações não commitadas

- `compose.yaml`, `.env.example`, `README.md` e `docs/deployment.md`: ajustes de execução local e VPS.
- `server/src/main/resources/db/migration/V23__ites_domain.sql`.
- `server/src/main/java/br/com/vhstudio/server/controllers/Ites/`: controladores de demanda, organizações, páginas, permissões, usuários e papéis.
- `server/src/main/java/br/com/vhstudio/server/services/Ites/`: autorização e regras de demanda.
- `server/src/main/java/br/com/vhstudio/server/configs/Security/SecurityConfig.java`: rota pública de leitura de páginas publicadas.
- `web/src/router/Router.tsx` e `web/src/pages/ites/`: portal e jornadas ITES.
- `database/bootstrap_admin.sql`: atribuição inicial manual de administrador.

## Validações observadas

- A configuração Compose foi analisada com `docker compose --env-file .env.example -f compose.yaml config --quiet` antes dos últimos ajustes de ambiente; repetir após revisão.
- A instalação de dependências do frontend terminou. Uma execução de `npm run typecheck`, `npm test` e `npm run build` passou em versões anteriores das telas ITES: 50 testes passaram e o build de produção terminou. Foram adicionadas telas e alterações depois dessa execução; repetir a validação para o estado atual.
- As migrações Flyway V1–V23 foram concatenadas e aplicadas com uma transação por migração em um PostgreSQL 17 temporário; a execução terminou com sucesso após a inclusão de V23. Isso valida sintaxe e dependências em PostgreSQL 17, não em PostgreSQL 16 nem pela própria inicialização do Flyway.
- Foram inseridos dados temporários para verificar código único, estado inicial e rejeição da transição direta inválida. A tentativa de `capacity=OTHER` sem descrição foi recusada pelo CHECK.
- A compilação Java e o build Docker do backend não terminaram. O ambiente não tinha Java instalado. Uma tentativa de instalar JDK 25 por `dnf` permaneceu lenta/em andamento na última observação; confira processos antes de iniciar outra instalação. O download das imagens Temurin no build Docker também estava lento. Não afirmar que a API compila.
- Não foi executado fluxo de navegador ou teste ponta a ponta. Não foi feita implantação em Dokploy/VPS.

## Limitações e riscos conhecidos

1. A integração base trouxe 774 arquivos e mais de 100 mil linhas do VH Studio. Revisar o diff e remover somente funcionalidades realmente dispensáveis depois de mapear suas dependências, mantendo a aplicação única e sem diretórios por idade.
2. Cadastro exige e-mail de confirmação. Com `MAILER_ENABLED=false`, o provedor descarta mensagens e novas contas não concluem cadastro. O guia local precisa de credencial válida do Resend ou de um mecanismo local seguro de entrega de links.
3. A configuração do banco na aplicação usa uma conta proprietária para Flyway e runtime; privilégios mínimos ainda não foram implementados na stack Compose. Separar credenciais de migração e runtime e verificar acesso por tabela/rotina.
4. Os scripts acadêmicos em `database/coursework/` ainda são um modelo separado e não correspondem ao esquema real V1–V23. O diagrama também não cobre as 35 tabelas físicas atuais. Reconciliar consultas, rotinas, carga de 20 registros, modelo e documentação com a base efetiva; não manter duas fontes autoritativas de migração.
5. A trilha de auditoria de transições é escrita pelo serviço na mesma transação. O usuário runtime de banco pode fazer DML direto; assegurar que as permissões e rotinas impeçam alteração de situação sem histórico.
6. A UI de editor integra operações e renderização de blocos do `web-builder`, mas ainda não cobre todo o ciclo original nem executou persistência e publicação após reinicialização.
7. Notícias, eventos, publicações, biblioteca e conteúdo institucional ainda são cartões estáticos; apenas páginas institucionais versionadas podem ser editadas pelo editor.
8. O PDF de requisitos original não foi encontrado. O código e a documentação usam a especificação reproduzida na solicitação.

## Próximos passos para retomar

1. Preservar todas as alterações do diretório de trabalho e conferir processos Docker/`dnf` ativos.
2. Rodar `npm run typecheck`, `npm test`, `npm run build` em `web/` após as telas ITES atuais.
3. Concluir compilação backend com JDK 25, corrigir erros e executar `./mvnw verify`.
4. Construir e iniciar `docker compose up --build`, observar migrações e health checks, testar portal e login com configuração de e-mail.
5. Testar cadastro, organização, envio de demanda, titularidade, revisão, concessões e transições com banco real; corrigir defeitos antes de commits funcionais.
6. Testar salvar/reabrir/publicar/despublicar página e acesso público/negado após reinício.
7. Reconciliar o banco acadêmico e seus artefatos com o esquema físico; gerar diagrama e evidências da versão efetiva.
8. Atualizar rastreabilidade e documentação para estado implementado, então criar commits focados em português com identidade Git verificada.
9. Validar implantação Compose local. Dokploy/VPS permanece pendente de acesso à infraestrutura.

## Comandos úteis

```sh
cd web && npm run typecheck && npm test && npm run build
cd ../server && ./mvnw verify
cd .. && docker compose --env-file .env.example -f compose.yaml config
cp .env.example .env
# Depois de preencher .env com segredos válidos:
docker compose up --build
```

O roteiro é um registro de retomada, não uma declaração de conclusão do MVP.
