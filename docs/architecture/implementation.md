# Integração da base executável

O código de `web-builder` foi incorporado aos módulos `web/` e `server/` como base única de autenticação, sessões, editor, publicação e persistência. A integração preserva nomes de classes, componentes e rotas necessárias ao funcionamento dessas capacidades. O portal e os fluxos de demandas da ITES serão adicionados nessa mesma arquitetura. `server/src/main/resources/db/migration/` continua sendo a única fonte de migrações Flyway; `database/coursework/` contém scripts acadêmicos independentes, ainda a reconciliar com o esquema da aplicação.

A migração V23 acrescenta papéis e permissões da aplicação, demandas e páginas institucionais. O cadastro existente não concede papel administrativo; a atribuição inicial deve ser feita por procedimento operacional controlado. A incorporação ainda não significa que os fluxos da ITES estejam implementados ou validados.
