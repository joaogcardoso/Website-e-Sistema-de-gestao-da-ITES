# Implementation roadmap and validation plan

Every stage depends on approval of database proposal and preceding stage. Each requires passing checks and stored evidence before progression.

| Stage | Objective and deliverable | Acceptance/validation | Risk |
|---|---|---|---|
| 1 | DB foundation: Flyway and schema | clean migration, FK/check tests | schema drift |
| 2 | Auth/grants: registration, roles | login/logout, 403, revocation | privilege escalation |
| 3 | Portal: seven sections/content | published-only public rendering | leaked drafts |
| 4 | Requester profile/org | persistence and ownership | duplicate identity |
| 5 | Demand submission | code, Pendente, relogin | lost data |
| 6 | Admin review/decisions | filter, reasons, audit | invalid transition |
| 7 | Tracking | own data only | IDOR |
| 8 | Builder | save/reload/preview/publish | unsafe blocks |
| 9 | Automations | 3 procedures/functions/triggers | conflicting rules |
| 10 | DB security | roles, grants, denies | bypass via direct DML |
| 11 | Integration tests | requester/admin/builder E2E | environment |
| 12 | Final evidence/docs | real transcripts and mapping | incomplete evidence |

Use fresh PostgreSQL to apply migrations and demo seed; count all tables (>=20), execute 5 basic/3 JOIN/3 subqueries, procedures/functions/triggers including negative cases, role privilege checks, compare diagram to schema. Test requester registration/login/submit/relogin/foreign demand denial. Test admin accept/reject/status/audit and requester visibility. Test builder unauthorized denial, draft reload, publish and public render. Run typecheck/lint/unit/backend integration/build/startup/Playwright when available. Save commands, outputs and screenshots as real evidence; never use mocks for final E2E. Accessibility: keyboard navigation, labels, contrast and responsive viewport checks.
