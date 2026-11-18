# Plano de implementação e validação

Cada etapa depende da aprovação da proposta do banco e das etapas anteriores. Nenhuma etapa avança sem verificações e evidências registradas.

| Etapa | Objetivo e entrega | Aceite e validação | Risco |
|---|---|---|---|
| 1 | Base de dados com Flyway | migração limpa, testes de FK e CHECK | divergência do esquema |
| 2 | Autenticação e permissões | acesso, saída, 403 e revogação | elevação de privilégio |
| 3 | Portal com sete seções | somente publicação aparece | exposição de rascunho |
| 4 | Perfil e organização | persistência e titularidade | identidade duplicada |
| 5 | Envio de demanda | código, Pendente e novo acesso | perda de dados |
| 6 | Análise e decisão | filtro, motivo e auditoria | transição inválida |
| 7 | Acompanhamento | acesso apenas ao próprio registro | acesso indevido por ID |
| 8 | Editor visual | salvar, reabrir, prévia e publicação | bloco inseguro |
| 9 | Automações do banco | três procedimentos, funções e gatilhos | regras conflitantes |
| 10 | Segurança do banco | papéis, concessões e negativas | escrita direta indevida |
| 11 | Testes integrados | jornadas completas | ambiente indisponível |
| 12 | Evidências e documentação final | resultados reais e rastreabilidade | evidência incompleta |

Em PostgreSQL vazio, executar migrações e carga demonstrativa; contar todas as tabelas (mínimo 20), executar cinco consultas básicas, três JOINs e três subconsultas, testar procedimentos, funções e gatilhos com casos positivos e negativos, verificar privilégios e restrições e comparar imagem e esquema. Testar cadastro, autenticação, envio, persistência após novo acesso e negação de demanda alheia. Testar aceite, recusa, histórico e visibilidade para solicitante. Testar negação de edição não autorizada, reabertura do rascunho, publicação e renderização. Executar verificação de tipos, lint, testes unitários, integração de backend, build, inicialização e Playwright quando disponíveis. Guardar comandos, saídas e capturas reais; testes finais ponta a ponta não usarão respostas simuladas. Verificar teclado, rótulos, contraste e telas responsivas.
