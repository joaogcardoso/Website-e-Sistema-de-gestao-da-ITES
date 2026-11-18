# Proposta e documentação da base de dados

A proposta foi aprovada pelo responsável do projeto nesta conversa. Os scripts abaixo atendem ao modelo acadêmico proposto, mas permanecem independentes do schema operacional Flyway (`server/src/main/resources/db/migration/`), que inclui a base integrada do editor e as tabelas ITES. A reconciliação e validação acadêmica com a estrutura operacional ainda são pendências; não aplicar estes scripts como migrações da aplicação.

O SGBD escolhido é o **PostgreSQL**, o mesmo do `web-builder`. Um único banco relacional guarda identidade e permissões, demandas, conteúdo público do portal e versões das páginas do editor.

Scripts propostos (validados exploratoriamente em PostgreSQL 17, com limitações registradas em [evidências](evidence/validation-postgresql17.md)): [schema](../../database/coursework/01_schema.sql), [dados de demonstração](../../database/coursework/02_seed.sql), [consultas](../../database/coursework/03_queries.sql), [rotinas](../../database/coursework/04_routines.sql) e [segurança](../../database/coursework/05_security.sql).

## Dicionário de dados

| Tabela | Finalidade | Chaves e restrições |
|---|---|---|
| `users` | Usuários: identidade, contato, senha (hash), ativo e data de criação | PK `id`; `email` único |
| `roles` | Papéis de acesso | PK `id`; `code` único |
| `permissions` | Permissões do sistema | PK `id`; `code` único |
| `user_roles` | Papéis de cada usuário (N:N) | PK composta; FKs para `users` e `roles` |
| `role_permissions` | Permissões de cada papel (N:N) | PK composta; FKs para `roles` e `permissions` |
| `organizations` | Empreendimentos, grupos, fóruns e instituições: nome, setor econômico, contato | PK `id` |
| `demands` | Demandas: código, solicitante, organização (opcional), condição do solicitante, serviço, descrição, urgência, situação e nota de conclusão | PK `id`; `code` único; FKs para `users` e `organizations`; CHECKs de condição, urgência, situação, descrição obrigatória em "outros" e nota obrigatória na conclusão |
| `demand_events` | Histórico das mudanças de situação: situação anterior e nova, responsável, justificativa e data | PK `id`; FKs para `demands` e `users` |
| `pages` | Páginas do portal e versão publicada | PK `id`; `slug` único |
| `page_versions` | Versões das páginas: revisão, conteúdo JSONB, autor | PK `id`; FKs para `pages` e `users`; (página, revisão) única |
| `page_events` | Auditoria das publicações | PK `id`; FKs para `pages`, `page_versions` e `users` |
| `grant_audit` | Auditoria da atribuição de papéis | PK `id`; FKs para `users` |

Os tipos exatos, a obrigatoriedade e os valores padrão estão nos scripts SQL. O diagrama mostra as cardinalidades 1:N e N:N.

## APD1 — implementação da base

**Ordem de execução:** schema, rotinas, dados, consultas e segurança.

**Dados:** pelo menos 20 registros em cada tabela. Os dados de demonstração atuais precisam de revisão: são genéricos ("Papel demonstrativo 1"), dependem de rotinas do APD2 e o gatilho de auditoria das páginas exige o responsável informado na sessão.

**Consultas previstas:**

- **5 simples:** demandas pendentes, demandas de alta urgência, organizações de um setor, demandas em um período e páginas publicadas.
- **3 com JOIN:** total de demandas por organização e situação, decisões por responsável, total por setor e serviço.
- **3 subconsultas:** organizações com demanda aceita (EXISTS), solicitantes acima da média de demandas (comparação) e solicitantes com demanda urgente de organizações de artesanato (IN).

## APD2 — tarefas e automatizações

- **Functions:** contar demandas por situação, contar demandas de um solicitante e contar páginas publicadas.
- **Procedures:** mudar a situação da demanda de forma atômica com registro no histórico; publicar uma versão válida de página; atribuir papel com auditoria.
- **Triggers:** validar as transições de situação e atualizar a data; impedir alteração ou exclusão do histórico; auditar publicações.
- **Testes de sucesso:** transição válida, publicação válida, atribuição de papel, contagens.
- **Testes de erro:** transição inválida, justificativa vazia, versão inválida, alteração do histórico, conclusão sem nota.

O serviço da aplicação verifica a permissão antes de chamar a rotina; as regras do banco não podem conflitar com as do serviço.

## APD3 — administração, segurança e integridade

- **Papéis do banco:** `ites_runtime` (operações da aplicação e rotinas), `ites_reporting` (somente leitura para relatórios) e `ites_migration` (dono do schema, usado nas migrações).
- **Usuários de login:** criados com senhas definidas fora do repositório.
- **Testes:** revogar o acesso de PUBLIC ao schema; verificar leituras e escritas permitidas; verificar que o papel de relatório não escreve e que o papel da aplicação não altera a estrutura; verificar a rejeição por chaves estrangeiras e CHECKs.
- **Falha de segurança identificada:** o privilégio de `UPDATE` direto nas tabelas, dado ao papel da aplicação, permite burlar as procedures. Deve ser corrigido com privilégios por coluna ou rotinas com dono dedicado antes do uso.

Os papéis do banco de dados são diferentes das permissões dos usuários dentro do sistema.

## Evidências

Uma validação exploratória em PostgreSQL 17 foi executada; consulte [evidências](evidence/validation-postgresql17.md). Ainda é necessário registrar saídas reais em PostgreSQL 16 para produção e executar os cenários restantes. Registrar saídas reais do `psql` para criação, contagem, 11 consultas, três procedimentos, funções, gatilhos, privilégios e restrições. Comparar `information_schema` com a imagem. Os hashes demonstrativos não permitem autenticação.
A execução exploratória está em `evidence/`. Ainda faltam validação na versão de implantação, testes de autorização completos e comparação automatizada entre esquema e diagrama. As senhas de demonstração não permitem login.

## Ajustes planejados

- Preservar identificadores programáticos em inglês, conforme a política do projeto.
- Dados de demonstração realistas (organizações, serviços e demandas do contexto da economia solidária).
- Criar os usuários de login do banco, exigidos pelo APD3.
- Corrigir a falha de segurança do papel da aplicação.
- Executar tudo e gerar as evidências.
