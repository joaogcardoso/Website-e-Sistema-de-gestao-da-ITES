# Database proposal — approval pending

PostgreSQL matches `web-builder`. One relational database holds identity, demands, public content and builder versions. [Schema](../../database/coursework/01_schema.sql), [demo seed](../../database/coursework/02_seed.sql), [queries](../../database/coursework/03_queries.sql), [routines](../../database/coursework/04_routines.sql), [security](../../database/coursework/05_security.sql) are proposed and unexecuted. Source PDF/template absent.

## Data dictionary

`users`: identity/contact/hash/active/time, PK id, unique email. `roles`: PK id, unique code, name. `permissions`: PK id, unique code, description. `user_roles`: composite PK and FKs to users/roles. `role_permissions`: composite PK and FKs to roles/permissions. `organizations`: PK id, name/sector/contact/time. `demands`: PK id, unique code, requester and optional organization FKs, capacity/service/description/urgency/status/completion/time with checks. `demand_events`: PK id, demand/actor FKs, before/after/reason/time. `pages`: PK id, unique slug, title, published pointer. `page_versions`: PK id, page/author FKs, revision, JSONB document/time, unique page/revision. `page_events`: PK id, page/version/actor FKs, action/time. `grant_audit`: PK id, actor/subject FKs, action/time. Exact column types/nullability/defaults are canonical in SQL. Diagram shows 1:N and M:N cardinalities.

## ADP1

Order: schema, routines, seed, queries, security. Demo requires 20 records in every table; `grant_audit` needs 20 `assign_role` calls. Current seed is a proposal and needs revision to avoid the page audit trigger requiring actor context and to populate the audit table. Role/permission catalogs use 20 demonstration entries for coursework; production must use meaningful permissions. Five basic queries: pending, high urgency, organization sector, date range, published content. Three JOINs: organization/status totals, decisions by actor, sector/service totals. Three subqueries: accepted organization, above-average requester, high-urgency handicraft requester. Capture real results after execution.

## ADP2

Three functions count demands by status, requester demands and published pages. Three procedures transition a demand atomically with event, publish a valid version, and assign a role with audit. Three triggers validate status graph/timestamp, prohibit event changes, and audit publication. Positive cases: legal transition, valid publish, assignment, counts. Negative: illegal transition, blank reason, invalid version, event mutation, completion without note. App service checks grant before calling. Rules must not conflict with service logic.

## ADP3

Database roles: runtime (operational DML and routines), reporting (SELECT), migration (DDL owner provisioning). Users/logins provisioned externally with environment secrets. Revoke PUBLIC schema access; verify authorized reads/writes, denied reporting writes/runtime DDL, FK/check rejection. **Security gap:** proposed runtime table UPDATE grants allow bypassing procedures; refine with column grants or dedicated routine ownership before deployment. Database roles differ from application user grants.

## Evidence

No SQL execution occurred. Record actual psql transcripts for creation, counts, 11 queries, 3 procedures, 3 functions, 3 triggers, privileges and constraints. Compare `information_schema` with diagram. Demo hashes are non-loginable.
