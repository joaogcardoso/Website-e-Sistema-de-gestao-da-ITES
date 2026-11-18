-- Five basic queries
SELECT code,created_at FROM demands WHERE status='Pendente' ORDER BY created_at DESC;
SELECT code,service FROM demands WHERE urgency='HIGH' ORDER BY code;
SELECT name,economic_sector FROM organizations WHERE economic_sector='artesanato' ORDER BY name;
SELECT code,status FROM demands WHERE created_at>=DATE '2020-01-01' AND created_at<DATE '2100-01-01' ORDER BY created_at;
SELECT slug,title FROM pages WHERE published_version_id IS NOT NULL ORDER BY slug;
-- Three joins
SELECT o.name,d.status,count(*) AS total FROM organizations o JOIN demands d ON d.organization_id=o.id GROUP BY o.id,o.name,d.status ORDER BY o.name,d.status;
SELECT u.name,count(*) AS decisions FROM users u JOIN demand_events e ON e.actor_id=u.id WHERE e.new_status IN ('Aceita para atendimento','Recusada') GROUP BY u.id,u.name ORDER BY decisions DESC;
SELECT o.economic_sector,d.service,count(*) AS total FROM organizations o JOIN demands d ON d.organization_id=o.id GROUP BY o.economic_sector,d.service ORDER BY total DESC;
-- Three subqueries
SELECT o.name FROM organizations o WHERE EXISTS(SELECT 1 FROM demands d WHERE d.organization_id=o.id AND d.status='Aceita para atendimento');
SELECT u.name FROM users u WHERE (SELECT count(*) FROM demands d WHERE d.requester_id=u.id) > (SELECT avg(c) FROM (SELECT count(*) AS c FROM demands GROUP BY requester_id) q);
SELECT u.name FROM users u WHERE u.id IN (SELECT d.requester_id FROM demands d WHERE d.urgency='HIGH' AND d.organization_id IN (SELECT o.id FROM organizations o WHERE o.economic_sector='artesanato'));
