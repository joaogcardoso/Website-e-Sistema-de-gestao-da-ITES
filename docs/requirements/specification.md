# Requirements specification and traceability

ITES at IFRJ Campus Niterói supports solidarity economy groups, education, events and partnerships. Actors: public visitor, requester, demand reviewer, administrator, builder editor and publisher. Affiliation confers no administrative grant. Public content requires publication; personal and operational data stay private.

Requester registers with name, contact and password, logs in, maintains profile, submits multiple demands, selects capacity per demand (group, forum, partner, other with required description), associates an organization where applicable, receives a unique code and tracks only own decisions. Admin filters/classifies and decides with explicit grant and reason. Builder editor edits allowed blocks, saves and reloads draft; publisher approves separately. Economic sector differs from requested service.

Transitions: Pendente → Aceita para atendimento, Recusada or Cancelada; Aceita para atendimento → Em atendimento or Cancelada; Em atendimento → Concluída or Cancelada. Decisions need actor, date, reason. Completion needs note; rejection/cancellation reason retained. Transition and audit atomic.

| ID | Requirement | Target | Acceptance |
|---|---|---|---|
| M1 | Profile, organization, per-demand capacity | users/organizations/demands | same user submits for two groups |
| M2 | Coded persistent demand, service, urgency, Pendente | demand API/UI | code and relogin persistence |
| M3 | Auth, sessions, grants | security | unauthorized 403 |
| M4 | List/filter/detail/classification | admin | status filter and associations |
| M5 | Decisions and trace | transition/history | actor/time/reason; invalid fails |
| M6 | Own tracking | requester | other user's record inaccessible |
| M7 | Planning | future boundary | documented extension |
| M8 | Reports | SQL metrics; future PDF | metrics verified |
| M9 | Notifications | in-app status; future outbox | status visible |
| P1 | Seven public sections and publication | pages/versions | draft hidden |
| B1 | Builder edit/preview/save/publish/reload | editor/page API | published rendering |
| G1 | Role grants/revocation | auth tables | backend enforcement |
| D1 | Model and 20 rows/table | schema/seed | counts/FKs verified |
| D2 | 5 basic, 3 JOIN, 3 subqueries | queries | results captured |
| D3 | 3 procedures/functions/triggers | routines | positive/negative tests |
| D4 | DB roles/privileges | security | denied operations |
| DOC | dictionary, image, evidence | docs | actual schema matches |
