# Portal institucional e gestão de demandas da ITES

Aplicação web da Incubadora Tecnológica de Economia Solidária do IFRJ Campus Niterói. O repositório contém `web/` (React e editor visual), `server/` (Spring Boot, autenticação e API), `database/` (scripts acadêmicos e de administração) e `docs/` (documentação técnica). As migrações executadas pela aplicação ficam exclusivamente em `server/src/main/resources/db/migration/`.

## Execução local com Docker

Pré-requisitos: Docker Engine e Docker Compose. Copie o modelo de configuração e gere dois segredos distintos:

```sh
cp .env.example .env
openssl rand -base64 32
openssl rand -base64 32
```

Defina em `.env` a senha do banco e os valores gerados em `AUTH_JWT_SECRET` e `AUTH_TOTP_ENCRYPTION_KEY`. Para receber e-mails de confirmação de cadastro, configure `MAILER_ENABLED=true`, `RESEND_API_KEY` e `MAIL_FROM_ADDRESS` de um domínio autorizado. Com o envio de e-mails desativado, o cadastro por link de confirmação não pode ser concluído; as contas existentes ainda podem entrar.

Inicie os serviços:

```sh
docker compose up --build -d
docker compose ps
docker compose logs --tail=100 server
```

O portal fica em `http://localhost:3000`; a API é encaminhada por `/api/` no mesmo endereço. A verificação do frontend é `http://localhost:3000/health`, e a da API é `http://localhost:3000/api/actuator/health`. O PostgreSQL não publica porta no host. O volume `postgres_data` preserva dados entre recriações dos contêineres. Não execute `docker compose down -v` se desejar manter os dados.

Após cadastrar uma conta institucional e confirmar seu e-mail, atribua o primeiro papel de administrador com acesso ao banco do contêiner:

```sh
docker compose exec -T postgres psql -U ites -d ites -v admin_email='admin@example.org' < database/bootstrap_admin.sql
```

Substitua `admin@example.org` pelo endereço real. Depois disso, o administrador poderá conceder e revogar papéis na interface. O vínculo com uma organização não concede permissões administrativas.

A página inicial é pública. O solicitante usa `/cadastro`, `/entrar` e `/demandas`. A equipe autorizada acessa `/administracao/demandas`, `/administracao/permissoes` e `/administracao/editor`. A API valida identidade e permissão no servidor.

## Verificação local

```sh
cd web && npm ci && npm run typecheck && npm run lint -- --max-warnings=0 && npm test && npm run build
cd ../server && ./mvnw verify
```

Os testes de backend usam Testcontainers e precisam de Docker. A pipeline de PR executa também o build das imagens Docker; veja [CI e proteção de PR](docs/ci.md). Consulte [documentação](docs/INDEX.md), [contribuição](CONTRIBUTING.md), [implantação](docs/deployment.md) e [estado de implementação](docs/IMPLEMENTATION_STATUS.md).

## Equipe

_[João Gabriel Souza Cardoso](https://github.com/joaogcardoso)_
_[Kauã Fiuza Clemente](https://github.com/kauafiuza)_
_[Victor Hugo Lima Monteiro](https://github.com/vhlima1008)_
