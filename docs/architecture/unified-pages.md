# Gestão unificada de páginas do ITES

## Diagnóstico em 19/11/2026

O editor principal utiliza `projects.document`, revisões imutáveis e `publications`. O portal institucional utiliza `institutional_pages` e `institutional_page_versions`. O painel de demandas utiliza essas mesmas tabelas institucionais sob o slug `painel-demandas`. As rotas operacionais e de autenticação são declaradas diretamente em `web/src/router/Router.tsx`. Portanto, o requisito de um projeto ITES único com todas as páginas gerenciadas pelo editor principal ainda não está implementado.

## Contrato arquitetural proposto

O projeto institucional ITES deve ser provisionado uma única vez em `projects`, com rota pública `/` e página inicial no documento do editor principal. As páginas operacionais devem receber uma representação gerenciada nesse projeto, com componentes de aplicação registrados e propriedades limitadas. A execução de autenticação, demandas, revisões e auditoria continua nos serviços existentes; o documento controla composição, apresentação e localização do componente.

Cada publicação deve registrar visibilidade `PUBLIC` ou `PRIVATE`, versão, responsável, data e concessões exigidas. A resolução no servidor verifica a rota, recupera a versão publicada e autoriza projeto, página e fontes de dados antes de devolver o documento. Conteúdo privado não deve aparecer em listagens públicas. Rotas de sistema e API não podem ser reservadas por páginas configuráveis.

## Migração necessária

| Área | Estado atual | Integração necessária |
|---|---|---|
| Portal inicial e páginas institucionais | Documento versionado em `institutional_pages` | Incorporar ao projeto ITES e preservar histórico publicado |
| Autenticação e conta | Rotas React próprias | Gerenciar apresentação e extensão visual; preservar componentes e validação de autenticação |
| Demandas e organizações | Telas React com API protegida | Registrar componentes funcionais e controlar composição por página |
| Revisões e auditoria | Telas React com grants organizacionais | Registrar componentes protegidos e exigir autorização no servidor |
| Indicadores e relatórios | Painel versionado e telas de exportação | Unificar páginas, fontes de dados permitidas e permissões de publicação |
| Projetos e editor | Editor principal com `projects` e `publications` | Usar como ambiente administrativo central |

## Sequência de implementação

1. Definir migração relacional de páginas, visibilidade, rota e concessões com tratamento de colisões.
2. Provisionar o projeto ITES e importar a versão institucional publicada sem perder o histórico.
3. Criar resolução de página no servidor e publicar páginas públicas e privadas com escolha explícita de visibilidade.
4. Integrar os componentes operacionais ao registro do editor, preservando os endpoints e regras de negócio.
5. Substituir as rotas de apresentação independentes após validação de cada jornada.
6. Executar testes de navegador para publicação, redirecionamento, resposta 404, persistência e regressão operacional.

Este documento descreve a direção e as lacunas observadas; não declara a migração como concluída.
