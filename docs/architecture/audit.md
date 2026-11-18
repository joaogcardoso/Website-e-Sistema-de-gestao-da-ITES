# Auditoria do repositório

## Situação inicial

O repositório começou apenas com uma estrutura básica: um README e cinco arquivos de marcação, sem aplicação, sem banco de dados, sem testes e sem arquivos de configuração. Os comentários desses arquivos indicavam a finalidade das pastas `docs` (documentação), `database` (banco de dados) e `modelos` (modelos de dados). Essas finalidades foram mantidas na documentação, nos scripts SQL e na imagem do modelo. Os arquivos `src` e `tests` estavam vazios. Os arquivos de marcação foram removidos para que as pastas pudessem usar esses nomes.

## Projeto de referência: `web-builder` (VH Studio)

O `web-builder` é um editor visual de sites já desenvolvido por um integrante da equipe, com:

- **Frontend:** React, TypeScript e Vite;
- **Backend:** Spring Boot, Java, JdbcTemplate e Flyway;
- **Banco de dados:** PostgreSQL, com Docker Compose.

O editor usa os módulos `web/src/pages/editor/lib/{document,registry,use-builder,properties}.ts`, um catálogo de blocos, a área de edição (Canvas), o editor de propriedades (PropertyEditor) e a pré-visualização (PreviewPage). No backend, os serviços de projetos, publicações, modelos, organizações e permissões guardam revisões e publicações. As migrações Flyway V1 a V22 cobrem usuários, sessões, projetos, organizações, permissões por escopo, modelos, publicações e auditoria. O projeto tem testes e integração contínua.

## O que será reaproveitado

- **Reaproveitar:** operações de documento, blocos selecionados, Canvas, editor de propriedades, pré-visualização e os conceitos de revisão e publicação.
- **Adaptar:** as permissões por projeto e organização para os perfis da ITES, mantendo Spring Security, Flyway e PostgreSQL.
- **Não usar:** funcionalidades de exportação e fluxos de produto do builder que não se aplicam à ITES.

## Riscos

Toda a aplicação da ITES ainda precisa ser desenvolvida. Os principais riscos são o acoplamento com o código do builder e a aprovação da proposta da base de dados.
