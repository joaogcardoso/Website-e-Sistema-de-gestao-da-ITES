# Editor visual de páginas institucionais

## Escopo funcional

O editor integrado permite abrir páginas institucionais, organizar blocos disponíveis, editar propriedades suportadas, visualizar a prévia, salvar rascunhos e publicar versões autorizadas. As operações utilizam a identidade, as permissões e a persistência da plataforma ITES.

## Blocos e validação

O catálogo é controlado pelo frontend e pelas definições de contrato utilizadas pela API. O servidor valida tipos de bloco, propriedades aceitas, URLs e tamanho do documento. Blocos desconhecidos não podem ser publicados. Documentos são dados JSON; nenhum código fornecido pelo usuário é executado.

## Ciclo de publicação

1. A equipe autorizada abre uma página institucional.
2. O editor carrega a versão atual e permite alterações apenas nos blocos suportados.
3. O rascunho é salvo com controle de revisão.
4. A prévia renderiza o documento validado sem alterar a versão pública.
5. Um usuário com a concessão de publicação ativa publica a versão.
6. A página pública consulta somente conteúdo publicado.
7. As alterações podem ser reabertas para edição e publicadas em nova revisão.

## Limites de edição e autorização

| Operação | Concessão |
|---|---|
| Abrir o rascunho | `builder.access` e `builder.pages.edit` |
| Salvar rascunho | `builder.access`, `portal.content.manage` e `builder.pages.edit` |
| Publicar ou retirar do ar | `builder.access`, `portal.content.manage` e `builder.pages.publish` |
| Gerenciar modelos | `builder.templates.manage` |

A autenticação, os formulários de demandas, a revisão de pedidos e os dados pessoais não são áreas editáveis. O frontend apresenta ações conforme as concessões efetivas, enquanto a API é responsável pela autorização final.

## Segurança

- Conteúdo não confiável é validado e renderizado pelo conjunto controlado de componentes.
- Scripts, propriedades arbitrárias e atributos executáveis não são aceitos.
- Endereços externos devem obedecer às regras de URL segura.
- Rascunhos não são expostos ao público.
- Publicação e auditoria persistem em conjunto.

## Página inicial e painel de demandas

A página `inicio` recebe um documento inicial versionado a partir de `ites-homepage.json` quando o administrador inicial já foi provisionado e ainda não existe uma versão pública. O portal lê essa versão publicada e renderiza apenas seus blocos; não mantém uma segunda composição institucional fixa em código. O editor usa o catálogo e o `PropertyEditor` do construtor já integrado. Na área institucional, o servidor permite categorias de conteúdo selecionadas, valida contratos dos manifests e limita quantidade e tamanho de propriedades. A ação de recuperar a versão publicada cria um novo rascunho versionado.

Na página pública `/`, uma sessão autenticada com `builder.access` e `builder.pages.edit` recebe o link **Editar página inicial**. O seletor do editor identifica o slug `inicio` como **Página inicial do ITES**. A edição continua protegida no servidor por `builder.access`, `portal.content.manage` e `builder.pages.edit`; a publicação também exige `builder.pages.publish`. A última versão publicada permanece visível ao público durante alterações em rascunho. A navegação condicional serve apenas para descoberta: chamadas sem grants são negadas pela API.

O painel `/administracao/indicadores` compartilha o armazenamento `institutional_pages` e `institutional_page_versions` sob o slug privado `painel-demandas`. Seus blocos usam fontes e visualizações enumeradas pelo servidor e consultam endpoints agregados protegidos; o documento não contém SQL nem cópias dos dados. A configuração exige `dashboards.manage`, enquanto os dados exigem `analytics.view`. O painel não é publicado como página pública.

Os documentos do portal e os do painel têm validações distintas, porque o formato e a finalidade são distintos, mas compartilham as mesmas tabelas de versões e auditoria do ciclo de páginas. As APIs controladas aplicam uma allowlist de categorias e de fontes de dados em vez de aceitar componentes executáveis.
