# Especificação de requisitos e rastreabilidade

## Contexto

A ITES, no IFRJ Campus Niterói, apoia grupos de economia solidária com assessoria, formação, eventos e parcerias. Referência: *Proposta de Desenvolvimento do Portal Web e Sistema de Gestão da ITES*.

## Atores

Visitante, solicitante, analista de demandas, administrador, editor de conteúdo e publicador. O vínculo institucional (coordenador, professor colaborador, bolsista, voluntário) **não** concede permissão administrativa automaticamente. O conteúdo público só aparece depois de publicado; dados pessoais e operacionais são privados.

## Regras principais

- O solicitante se cadastra com nome, contato (e-mail ou WhatsApp) e senha, faz login, mantém seu perfil e pode enviar várias demandas.
- Em cada demanda, o solicitante informa sua **condição**: representante de empreendimento/grupo/coletivo, integrante de fórum, representante de instituição parceira ou outros (com descrição obrigatória). Quando for o caso, associa uma organização.
- Cada demanda recebe um **código único**, começa na situação **Pendente**, e o solicitante só acompanha as próprias demandas e decisões.
- O administrador filtra, classifica e decide as demandas, com permissão explícita e justificativa.
- O editor de conteúdo edita blocos permitidos, salva e recarrega rascunhos. A publicação é aprovada separadamente pelo publicador.
- O **setor econômico** da organização é diferente do **serviço solicitado** à ITES.

## Transições de situação da demanda

| De | Para |
|---|---|
| Pendente | Aceita para atendimento, Recusada ou Cancelada |
| Aceita para atendimento | Em atendimento ou Cancelada |
| Em atendimento | Concluída ou Cancelada |

Toda decisão registra responsável, data e justificativa. A conclusão exige uma nota de conclusão. Os motivos de recusa e cancelamento ficam guardados. A transição e o registro no histórico acontecem juntos (de forma atômica).

## Rastreabilidade

| ID | Requisito | Onde é atendido | Critério de aceite |
|---|---|---|---|
| M1 | Perfil, organização e condição por demanda | usuários / organizações / demandas | o mesmo usuário envia demandas por dois grupos diferentes |
| M2 | Demanda com código, serviço, urgência e situação Pendente | API e telas de demanda | código gerado e dados mantidos após novo login |
| M3 | Autenticação, sessões e permissões | segurança | acesso sem permissão retorna 403 |
| M4 | Listar, filtrar, detalhar e classificar | área administrativa | filtro por situação e associações corretas |
| M5 | Decisões e histórico | transição e histórico | responsável, data e motivo registrados; transição inválida falha |
| M6 | Acompanhamento das próprias demandas | área do solicitante | demanda de outro usuário inacessível |
| M7 | Planejamento das atividades | evolução futura | extensão documentada |
| M8 | Relatórios | métricas em SQL; PDF no futuro | métricas conferidas |
| M9 | Notificações | situação visível no sistema; e-mail/WhatsApp no futuro | situação exibida ao solicitante |
| P1 | Sete seções públicas e publicação | páginas e versões | rascunho não aparece para o público |
| B1 | Editar, pré-visualizar, salvar, publicar e recarregar páginas | editor e API de páginas | página publicada exibida corretamente |
| G1 | Atribuição e revogação de papéis | tabelas de autorização | regra garantida pelo backend |
| D1 | Modelo de dados e 20 registros por tabela | schema e dados | contagens e chaves estrangeiras conferidas |
| D2 | 5 consultas simples, 3 com JOIN e 3 subconsultas | consultas | resultados registrados |
| D3 | 3 procedures, 3 functions e 3 triggers | rotinas | testes de sucesso e de erro |
| D4 | Papéis e privilégios do banco | segurança | operações não permitidas são negadas |
| DOC | Dicionário de dados, imagem do modelo e evidências | documentação | schema real igual ao documentado |
