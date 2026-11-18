# Arquitetura proposta

```mermaid
flowchart LR
 Visitante --> React[Portal e aplicação React]
 Solicitante --> React
 Administrador --> React
 React --> API[API Spring Boot]
 API --> Seguranca[Spring Security e permissões]
 API --> Dominio[Serviços de portal, demandas e editor]
 Dominio --> JDBC[Repositórios JdbcTemplate]
 JDBC --> PG[(PostgreSQL)]
```

Haverá um frontend, uma API e um banco de dados. Rotas públicas lerão apenas versões publicadas. Rotas do solicitante obterão o proprietário da sessão autenticada. Rotas administrativas verificarão permissões explícitas. O serviço de demandas chamará a rotina canônica de transição após conferir a permissão. Documentos JSON do editor serão validados por uma lista de blocos permitidos; rascunho e publicação terão referências distintas; revisões otimistas evitarão perda de alterações. Autenticação, formulários de demanda e decisões serão componentes fixos. Listas terão paginação; índices atenderão proprietário, situação e data; documentos terão limite de tamanho e não aceitarão conteúdo executável.

ADR-001: React, Spring e PostgreSQL com Flyway seguem as convenções do `web-builder`. ADR-002: haverá um único sistema de identidade e persistência. ADR-003: a condição de representação pertence à demanda. ADR-004: a rotina do banco controla atomicamente situação e histórico. ADR-005: somente páginas institucionais de conteúdo serão inteiramente editáveis. ADR-006: dados demonstrativos ficarão separados das migrações de produção. Papéis do banco e permissões da aplicação são camadas distintas.
