# Situação atual da documentação — incompleta

Existem auditoria, requisitos, proposta arquitetural, especificação do editor visual, SQL proposto, imagem SVG do modelo lógico e plano de testes. O PDF de origem e o modelo institucional estão ausentes. A aprovação da proposta de banco está **pendente**. O SQL proposto passou por validação exploratória em PostgreSQL 17, com [evidências](database/evidence/validation-postgresql17.md), mas ainda não foi validado no PostgreSQL 16 previsto nem integrado ao aplicativo. Os scripts ainda têm lacunas de segurança para produção e precisam ser corrigidos e testados. Nenhum comportamento da aplicação foi alterado. O pacote documental ainda não constitui um marco completo e validado; a implementação não começou. Este relatório não declara a conclusão do MVP.
**Pronto:** auditoria do repositório, especificação de requisitos, arquitetura proposta, regras do editor de páginas, proposta da base de dados (scripts SQL iniciais), imagem do modelo lógico e roteiro de entrega e testes. O documento de referência da ITES foi recebido e incorporado aos requisitos.

**Pendente:**

- aprovação da proposta da base de dados pelo professor;
- revisão dos scripts SQL (dados de exemplo realistas, correções de segurança e nomes em português) e execução no PostgreSQL;
- evidências reais de execução (criação das tabelas, dados inseridos, consultas, procedures, functions, triggers e testes de acesso);
- implementação da aplicação, que ainda não começou.

Este relatório não declara o MVP como concluído.
