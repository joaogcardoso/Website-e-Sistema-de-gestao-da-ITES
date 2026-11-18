# Website e Sistema de Gestão da ITES

Sistema web composto por um **portal institucional** da Incubadora Tecnológica de Economia Solidária (ITES – IFRJ Campus Niterói) e por um **sistema de gestão de demandas**. O objetivo é permitir que pessoas e organizações interessadas se conectem à ITES, encaminhem demandas, e que a equipe da incubadora analise, acompanhe e gere relatórios a partir desses dados.

## Contexto

A ITES apoia empreendimentos, grupos, coletivos e redes de economia solidária, promovendo trabalho, renda, formação, inclusão e desenvolvimento territorial em Niterói/RJ. Hoje os pedidos de apoio não têm registro centralizado, o que dificulta saber o que está pendente, quem decidiu cada pedido e quais resultados foram alcançados.

A solução prevê dois ambientes integrados:

- **Portal Web Institucional** — Quem Somos, Projetos e Empreendimentos Incubados, Notícias e Agenda, Publicações, Aplicativos e Ferramentas, Redes Sociais e Contato, e acesso à Solicitação de Demandas. O conteúdo é gerenciado por usuários autorizados, com rascunho e publicação.
- **Sistema de Gestão de Demandas** — cadastro de solicitantes e organizações, envio de demandas com código único, triagem, aceite ou recusa com justificativa, acompanhamento pelo solicitante e indicadores para a gestão.

## Documentação

O ponto de partida é o [índice da documentação](docs/INDEX.md).

| Documento | Conteúdo |
|---|---|
| [Especificação de requisitos](docs/requirements/specification.md) | Atores, regras de negócio, transições de situação e rastreabilidade dos requisitos |
| [Arquitetura proposta](docs/architecture/target.md) | Visão geral da solução e decisões de arquitetura |
| [Auditoria do repositório](docs/architecture/audit.md) | Situação inicial do repositório e o que será reaproveitado |
| [Integração com o editor de páginas](docs/builder/integration.md) | Regras do editor visual do portal |
| [Proposta da base de dados](docs/database/technical.md) | Dicionário de dados e plano das atividades APD1, APD2 e APD3 |
| [Roteiro de entrega e validação](docs/plans/delivery.md) | Etapas, critérios de aceite e riscos |
| [Situação atual](docs/CHECKPOINT.md) | O que já está pronto e o que falta |

## Tecnologias previstas

React + TypeScript (frontend), Spring Boot (API), PostgreSQL (banco de dados) e Flyway (migrações).

## Equipe

_[nomes dos integrantes]_
