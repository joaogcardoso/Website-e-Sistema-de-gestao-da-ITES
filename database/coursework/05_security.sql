-- O proprietário do banco executa este script; credenciais de login são provisionadas externamente.
CREATE ROLE ites_runtime NOLOGIN;
CREATE ROLE ites_reporting NOLOGIN;
CREATE ROLE ites_migration NOLOGIN;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT USAGE ON SCHEMA public TO ites_runtime,ites_reporting;
GRANT SELECT,INSERT,UPDATE ON users,organizations,demands,pages,page_versions,user_roles TO ites_runtime;
GRANT SELECT,INSERT ON demand_events,page_events,grant_audit TO ites_runtime;
GRANT SELECT ON roles,permissions,role_permissions TO ites_runtime;
GRANT USAGE,SELECT ON ALL SEQUENCES IN SCHEMA public TO ites_runtime;
GRANT EXECUTE ON ALL ROUTINES IN SCHEMA public TO ites_runtime;
GRANT SELECT ON organizations,demands,pages,page_versions TO ites_reporting;
REVOKE INSERT,UPDATE,DELETE ON ALL TABLES IN SCHEMA public FROM ites_reporting;
