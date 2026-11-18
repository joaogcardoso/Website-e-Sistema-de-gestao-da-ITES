-- Dados fictícios para demonstração; não executar em produção.
INSERT INTO users(name,email,password_hash,whatsapp) SELECT 'Pessoa Exemplo '||n,'person'||n||'@example.invalid','{DEMO_ONLY_NON_LOGINABLE}', '000000'||lpad(n::text,4,'0') FROM generate_series(1,25)n;
INSERT INTO roles(code,name) SELECT 'demo_role_'||n,'Papel demonstrativo '||n FROM generate_series(1,20)n;
INSERT INTO permissions(code,description) SELECT 'demo.permission.'||n,'Permissão demonstrativa '||n FROM generate_series(1,20)n;
INSERT INTO user_roles SELECT n,n FROM generate_series(1,20)n;
INSERT INTO role_permissions SELECT n,n FROM generate_series(1,20)n;
INSERT INTO organizations(name,economic_sector,contact_email) SELECT 'Coletivo Fictício '||n,(ARRAY['artesanato','agricultura','tecnologia','cultura'])[(n%4)+1], 'group'||n||'@example.invalid' FROM generate_series(1,20)n;
INSERT INTO demands(requester_id,organization_id,capacity,service,description,urgency,status,completion_note) SELECT n,n,'GROUP',(ARRAY['formação','comunicação','apoio administrativo'])[(n%3)+1],'Solicitação demonstrativa '||n,(ARRAY['LOW','MEDIUM','HIGH'])[(n%3)+1],(ARRAY['Pendente','Aceita para atendimento','Em atendimento','Concluída','Recusada','Cancelada'])[(n%6)+1],CASE WHEN n%6=3 THEN 'Atividade concluída' END FROM generate_series(1,20)n;
INSERT INTO demand_events(demand_id,actor_id,old_status,new_status,justification) SELECT n,25,'Pendente',(SELECT status FROM demands WHERE id=n),'Histórico demonstrativo '||n FROM generate_series(1,20)n;
INSERT INTO pages(slug,title) SELECT 'pagina-'||n,'Página demonstrativa '||n FROM generate_series(1,20)n;
INSERT INTO page_versions(page_id,revision,document,author_id) SELECT n,1,jsonb_build_object('blocks',jsonb_build_array(jsonb_build_object('type','richText','text','Conteúdo '||n))),25 FROM generate_series(1,20)n;
SELECT set_config('ites.actor_id','25',false);
UPDATE pages SET published_version_id=(SELECT id FROM page_versions WHERE page_id=pages.id);
DO $$ BEGIN FOR n IN 1..20 LOOP CALL assign_role(n,n,25); END LOOP; END $$;
