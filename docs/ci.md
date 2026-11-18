# Integração contínua e bloqueio de pull requests

O workflow `.github/workflows/ci.yml` executa em pull requests para `main`, em atualizações de `main` e manualmente. A etapa `CI / web` instala dependências e executa verificação TypeScript, testes e build. `CI / server` executa `./mvnw verify`, incluindo testes PostgreSQL via Testcontainers. `CI / deployment` valida o Compose e compila as imagens Docker. O job agregado `CI / ci-success` falha se qualquer etapa estiver com falha, cancelada ou ignorada.

Para bloquear efetivamente merge no GitHub, uma pessoa administradora do repositório precisa ativar proteção da branch ou ruleset para `main` e tornar `CI / ci-success` uma verificação obrigatória. Ative exigência de pull request, branch atualizada antes do merge e bloqueio de merges quando a verificação estiver pendente ou falhar. A configuração remota não pode ser criada pelo workflow e ainda precisa ser conferida na página de configurações do repositório.

O deploy não é executado pela CI. A etapa de imagens verifica que a configuração de produção pode ser construída. Segredos de produção não são fornecidos aos jobs.
