# Proposta de banco de dados — aprovação pendente

PostgreSQL é compatível com o `web-builder`. Um banco relacional conterá identidade, demandas, conteúdo público e versões do editor. [Esquema](../../database/coursework/01_schema.sql), [carga demonstrativa](../../database/coursework/02_seed.sql), [consultas](../../database/coursework/03_queries.sql), [rotinas](../../database/coursework/04_routines.sql) e [segurança](../../database/coursework/05_security.sql) são propostas não executadas. PDF e modelo institucional ausentes.

## Dicionário de dados

`users`: identidade, contato, hash da senha, situação e data; PK `id`, e-mail único. `roles`: PK `id`, código único e nome. `permissions`: PK `id`, código único e descrição. `user_roles`: PK composta e FKs para usuário e papel. `role_permissions`: PK composta e FKs para papel e permissão. `organizations`: PK `id`, nome, setor, contato e data. `demands`: PK `id`, código único, FKs para solicitante e organização opcional, condição, serviço, descrição, urgência, situação, conclusão e datas, com restrições. `demand_events`: PK `id`, FKs para demanda e responsável, situações anterior e posterior, motivo e data. `pages`: PK `id`, identificador único, título e referência publicada. `page_versions`: PK `id`, FKs para página e autor, revisão, documento JSONB e data; revisão única por página. `page_events`: PK `id`, FKs para página, versão e responsável, ação e data. `grant_audit`: PK `id`, FKs para responsável e beneficiário, ação e data. Tipos, nulidade e valores padrão exatos estão no SQL. A imagem apresenta cardinalidades 1:N e M:N.

## ADP1

Ordem proposta: esquema, rotinas, carga, consultas e segurança. A carga deve ter ao menos 20 registros em cada tabela; `grant_audit` recebe 20 chamadas de `assign_role`. O catálogo demonstrativo contém 20 papéis/permissões para a disciplina; produção deve usar permissões reais. Cinco consultas básicas listam pendentes, alta urgência, setor, intervalo de datas e páginas publicadas. Três JOINs contam demandas por organização/situação, decisões por responsável e setor/serviço. Três subconsultas localizam organizações aceitas, solicitantes acima da média e solicitantes de artesanato com alta urgência. Resultados reais devem ser registrados após execução.

## ADP2

Três funções contam demandas por situação, demandas do solicitante e páginas publicadas. Três procedimentos alteram demanda com histórico atômico, publicam versão válida e atribuem papel com auditoria. Três gatilhos validam transições e data, impedem alterações do histórico e auditam publicação. Casos positivos: transição válida, publicação, atribuição e contagens. Negativos: transição inválida, motivo vazio, versão incorreta, alteração de evento e conclusão sem informação final. O serviço deve verificar a permissão da aplicação antes de chamar a rotina. As regras não podem divergir entre serviço e banco.

## ADP3

Papéis do banco: execução da aplicação (`ites_runtime`), leitura para relatórios (`ites_reporting`) e migração (`ites_migration`). Credenciais de login serão provisionadas externamente por variáveis de ambiente. Acesso público ao esquema é revogado. Devem ser verificados leitura e escrita autorizadas, negação de escrita ao papel de relatórios e de DDL à aplicação, e falhas de FK/CHECK. **Lacuna de segurança:** o UPDATE geral da proposta permite contornar procedimentos; serão necessários privilégios por coluna ou propriedade controlada das rotinas antes da produção. Papéis do banco diferem das permissões dos usuários da aplicação.

## Evidências

Uma validação exploratória em PostgreSQL 17 foi executada; consulte [evidências](evidence/validation-postgresql17.md). Ainda é necessário registrar saídas reais em PostgreSQL 16 para produção e executar os cenários restantes. Registrar saídas reais do `psql` para criação, contagem, 11 consultas, três procedimentos, funções, gatilhos, privilégios e restrições. Comparar `information_schema` com a imagem. Os hashes demonstrativos não permitem autenticação.
