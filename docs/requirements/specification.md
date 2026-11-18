# Especificação de requisitos e rastreabilidade

A ITES, vinculada ao IFRJ Campus Niterói, apoia grupos de economia solidária, educação, eventos e parcerias. Os atores são visitante, solicitante, analista de demandas, administrador, editor visual e publicador. Vínculo institucional não concede administração. Conteúdo público depende de publicação; dados pessoais e operacionais permanecem protegidos.

O solicitante informa nome, contato e senha, autentica-se, mantém seu perfil, envia várias demandas, escolhe sua condição em cada demanda (grupo, fórum, instituição parceira ou outra com descrição obrigatória), associa uma organização quando couber, recebe código único e acompanha apenas suas próprias decisões. O administrador filtra, classifica e decide com permissão explícita e justificativa. O editor altera blocos permitidos, salva e reabre o rascunho; a publicação exige permissão própria. Setor econômico da organização e serviço solicitado são informações diferentes.

Transições: Pendente → Aceita para atendimento, Recusada ou Cancelada; Aceita para atendimento → Em atendimento ou Cancelada; Em atendimento → Concluída ou Cancelada. Decisões exigem responsável, data e justificativa. A conclusão exige informação final; recusa e cancelamento preservam a justificativa. Mudança de situação e histórico devem ser atômicos.

| ID | Requisito | Destino previsto | Critério de aceite |
|---|---|---|---|
| M1 | Perfil, organização e condição por demanda | `users`, `organizations`, `demands` | mesma pessoa solicita por dois grupos |
| M2 | Demanda persistente com código, serviço, urgência e situação inicial | API e interface de demanda | código exibido e demanda preservada após novo acesso |
| M3 | Autenticação, sessões e permissões | módulo de segurança | operação sem permissão retorna 403 |
| M4 | Lista, filtro, detalhe e classificação | área administrativa | filtro por situação e vínculos visíveis |
| M5 | Decisões válidas e rastreáveis | rotina de transição e histórico | responsável, data e motivo; transição inválida falha |
| M6 | Acompanhamento das próprias demandas | área do solicitante | demanda de terceiro inacessível |
| M7 | Planejamento de atividades | ponto de extensão futuro | limite arquitetural documentado |
| M8 | Relatórios | indicadores SQL; PDF futuro | indicadores verificados |
| M9 | Notificações | situação na aplicação; integração futura | situação atual visível |
| P1 | Sete seções públicas e publicação | páginas e versões | rascunho invisível ao público |
| B1 | Edição, prévia, salvamento, publicação e reabertura | editor e API de páginas | versão publicada renderizada |
| G1 | Concessão e revogação de permissões | tabelas de papéis e permissões | backend aplica permissão efetiva |
| D1 | Modelo relacional e 20 linhas por tabela demonstrativa | esquema e carga | contagens e FKs verificadas |
| D2 | Cinco consultas básicas, três JOINs e três subconsultas | SQL de consultas | resultados reais registrados |
| D3 | Três procedimentos, funções e gatilhos de cada tipo | SQL de rotinas | cenários positivos e negativos |
| D4 | Papéis e privilégios do banco | SQL de segurança | operações proibidas falham |
| DOC | Dicionário, imagem e evidências | `docs/database` | modelo corresponde ao esquema real |
